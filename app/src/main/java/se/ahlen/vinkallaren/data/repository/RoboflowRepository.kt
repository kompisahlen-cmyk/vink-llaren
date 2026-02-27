package se.ahlen.vinkallaren.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import se.ahlen.vinkallaren.data.remote.LabelDetectionResponse
import se.ahlen.vinkallaren.data.remote.LabelPrediction
import se.ahlen.vinkallaren.data.remote.RoboflowDataSource
import se.ahlen.vinkallaren.utils.ImageCropper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for Roboflow wine label detection operations
 * Orchestrates the full flow: detect -> crop -> return result
 */
@Singleton
class RoboflowRepository @Inject constructor(
    private val dataSource: RoboflowDataSource,
    private val imageCropper: ImageCropper
) {
    companion object {
        private const val TAG = "RoboflowRepository"
        private const val DEFAULT_CONFIDENCE_THRESHOLD = 0.5f
    }
    
    val isConfigured: Boolean
        get() = dataSource.isConfigured
    
    /**
     * Detect wine label and crop the image to the detected region
     * 
     * Flow:
     * 1. Send image to Roboflow API
     * 2. Get bounding box prediction
     * 3. Crop image to bounding box
     * 4. Return cropped image for OCR
     * 
     * @param bitmap Original image from camera
     * @return Result containing detection response and cropped bitmap
     */
    suspend fun detectAndCropLabel(bitmap: Bitmap): Result<LabelDetectionWithCrop> = 
        withContext(Dispatchers.Default) {
            try {
                // Step 1: Detect label using Roboflow
                Log.d(TAG, "Starting label detection...")
                val detectionResult = dataSource.detectLabel(bitmap)
                
                detectionResult.fold(
                    onSuccess = { response ->
                        // Step 2: Get best prediction
                        val bestPrediction = dataSource.getBestPrediction(response)
                        
                        if (bestPrediction == null) {
                            Log.w(TAG, "No label detected above confidence threshold")
                            return@withContext Result.success(
                                LabelDetectionWithCrop(
                                    response = response,
                                    prediction = null,
                                    croppedBitmap = null,
                                    confidence = 0f
                                )
                            )
                        }
                        
                        Log.d(TAG, "Label detected with confidence: ${bestPrediction.confidence}")
                        
                        // Step 3: Crop image to bounding box
                        val boundingBox = bestPrediction.toBoundingBox()
                            .clampToImage(bitmap.width, bitmap.height)
                        
                        val croppedBitmap = imageCropper.cropBitmap(bitmap, boundingBox)
                        
                        Log.d(TAG, "Image cropped successfully: ${croppedBitmap?.width}x${croppedBitmap?.height}")
                        
                        Result.success(
                            LabelDetectionWithCrop(
                                response = response,
                                prediction = bestPrediction,
                                croppedBitmap = croppedBitmap,
                                confidence = bestPrediction.confidence
                            )
                        )
                    },
                    onFailure = { error ->
                        Log.e(TAG, "Label detection failed", error)
                        Result.failure(error)
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error in detectAndCropLabel", e)
                Result.failure(e)
            }
        }
    
    /**
     * Detect label without cropping (for preview/debugging)
     */
    suspend fun detectLabelOnly(bitmap: Bitmap): Result<LabelDetectionResponse> {
        return dataSource.detectLabel(bitmap)
    }
    
    /**
     * For simple fallback - just returns if label was detected
     */
    suspend fun hasValidLabelDetection(bitmap: Bitmap): Boolean {
        return detectLabelOnly(bitmap).getOrNull()?.predictions
            ?.any { it.confidence >= DEFAULT_CONFIDENCE_THRESHOLD } ?: false
    }
}

/**
 * Result of label detection with optional cropped image
 */
data class LabelDetectionWithCrop(
    val response: LabelDetectionResponse,
    val prediction: LabelPrediction?,
    val croppedBitmap: Bitmap?,
    val confidence: Float
) {
    val hasDetection: Boolean
        get() = prediction != null && confidence >= 0.5f
    
    val hasCroppedImage: Boolean
        get() = croppedBitmap != null
}
