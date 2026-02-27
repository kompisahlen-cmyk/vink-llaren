package se.ahlen.vinkallaren.ui.viewmodel

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import se.ahlen.vinkallaren.data.repository.RoboflowRepository
import se.ahlen.vinkallaren.scanner.ScanResult
import se.ahlen.vinkallaren.scanner.WineScanner
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val wineScanner: WineScanner,
    private val roboflowRepository: RoboflowRepository
) : ViewModel() {
    
    companion object {
        private const val TAG = "ScannerViewModel"
    }
    
    private var imageCapture: ImageCapture? = null
    
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()
    
    private val _scannedData = MutableStateFlow<ScanResult.Success?>(null)
    val scannedData: StateFlow<ScanResult.Success?> = _scannedData.asStateFlow()
    
    private val _processingStep = MutableStateFlow<ProcessingStep>(ProcessingStep.Idle)
    val processingStep: StateFlow<ProcessingStep> = _processingStep.asStateFlow()
    
    private val _detectionConfidence = MutableStateFlow<Float?>(null)
    val detectionConfidence: StateFlow<Float?> = _detectionConfidence.asStateFlow()
    
    val isRoboflowConfigured: Boolean
        get() = roboflowRepository.isConfigured
    
    fun setImageCapture(capture: ImageCapture) {
        imageCapture = capture
    }
    
    fun captureImage() {
        val capture = imageCapture ?: return
        
        _scanState.value = ScanState.Processing
        _processingStep.value = ProcessingStep.Capturing
        
        capture.takePicture(
            executor = java.util.concurrent.Executors.newSingleThreadExecutor(),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    viewModelScope.launch {
                        val bitmap = imageProxyToBitmap(image)
                        processImage(bitmap)
                        image.close()
                    }
                }
                
                override fun onError(exception: ImageCaptureException) {
                    _scanState.value = ScanState.Error("Kunde inte ta bild: ${exception.message}")
                    _processingStep.value = ProcessingStep.Idle
                }
            }
        )
    }
    
    private suspend fun processImage(bitmap: Bitmap) {
        try {
            if (isRoboflowConfigured) {
                Log.d(TAG, "Using Roboflow-enhanced scan flow")
                _processingStep.value = ProcessingStep.DetectingLabel
                
                val detectionResult = roboflowRepository.detectAndCropLabel(bitmap)
                
                detectionResult.fold(
                    onSuccess = { labelDetection ->
                        _detectionConfidence.value = labelDetection.confidence
                        
                        if (labelDetection.hasCroppedImage) {
                            Log.d(TAG, "Label detected, running OCR on cropped image")
                            _processingStep.value = ProcessingStep.RecognizingText
                            
                            val croppedBitmap = labelDetection.croppedBitmap!!
                            val scanResult = wineScanner.scanLabel(croppedBitmap)
                            
                            handleScanResult(scanResult)
                        } else {
                            Log.w(TAG, "No label detected, falling back to full image OCR")
                            _processingStep.value = ProcessingStep.RecognizingText
                            val scanResult = wineScanner.scanLabel(bitmap)
                            handleScanResult(scanResult)
                        }
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Roboflow detection failed, falling back", error)
                        _processingStep.value = ProcessingStep.RecognizingText
                        val scanResult = wineScanner.scanLabel(bitmap)
                        handleScanResult(scanResult)
                    }
                )
            } else {
                Log.d(TAG, "Roboflow not configured, using standard OCR")
                _processingStep.value = ProcessingStep.RecognizingText
                val scanResult = wineScanner.scanLabel(bitmap)
                handleScanResult(scanResult)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing image", e)
            _scanState.value = ScanState.Error("Fel vid bildbehandling: ${e.message}")
            _processingStep.value = ProcessingStep.Idle
        }
    }
    
    private fun handleScanResult(result: ScanResult) {
        when (result) {
            is ScanResult.Success -> {
                _scannedData.value = result
                _scanState.value = if (result.extractedData.hasMinimumData()) {
                    ScanState.Success(result)
                } else {
                    ScanState.Error("Kunde inte läsa tillräckligt med information från etiketten")
                }
            }
            is ScanResult.Error -> {
                _scanState.value = ScanState.Error(result.message)
            }
        }
        _processingStep.value = ProcessingStep.Idle
    }
    
    fun processBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _scanState.value = ScanState.Processing
            processImage(bitmap)
        }
    }
    
    fun processBitmapDirectOcr(bitmap: Bitmap) {
        viewModelScope.launch {
            _scanState.value = ScanState.Processing
            _processingStep.value = ProcessingStep.RecognizingText
            val result = wineScanner.scanLabel(bitmap)
            handleScanResult(result)
        }
    }
    
    fun resetScan() {
        _scanState.value = ScanState.Idle
        _scannedData.value = null
        _processingStep.value = ProcessingStep.Idle
        _detectionConfidence.value = null
    }
    
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
        val buffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        
        val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val matrix = Matrix()
        matrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
        
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    
    sealed class ScanState {
        object Idle : ScanState()
        object Processing : ScanState()
        data class Success(val result: ScanResult.Success) : ScanState()
        data class Error(val message: String) : ScanState()
    }
    
    enum class ProcessingStep {
        Idle,
        Capturing,
        DetectingLabel,
        RecognizingText,
        SearchingSystembolaget
    }
}
