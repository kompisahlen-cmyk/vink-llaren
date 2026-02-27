package se.ahlen.vinkallaren.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import se.ahlen.vinkallaren.scanner.ScanResult
import se.ahlen.vinkallaren.ui.viewmodel.ScannerViewModel
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    navController: NavController,
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == 
            PackageManager.PERMISSION_GRANTED
        )
    }
    
    val scanState by viewModel.scanState.collectAsState()
    val scannedData by viewModel.scannedData.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Skanna etikett") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tillbaka")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!hasPermission) {
                // Request permission UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Kamerabehörighet krävs",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "För att skanna vinetiketter behöver vi tillgång till kameran",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                // Camera preview
                CameraPreview(viewModel)
                
                // Overlay UI
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Instructions
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                            .padding(16.dp)
                    ) {
                        Text(
                            "Håll kameran stilla över etiketten",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Capture button
                    Button(
                        onClick = { viewModel.captureImage() },
                        modifier = Modifier.padding(bottom = 32.dp),
                        enabled = scanState !is ScannerViewModel.ScanState.Processing
                    ) {
                        if (scanState is ScannerViewModel.ScanState.Processing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(Icons.Default.Camera, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Skanna")
                        }
                    }
                }
                
                // Results dialog
                when (val state = scanState) {
                    is ScannerViewModel.ScanState.Success -> {
                        ScanResultDialog(
                            result = state.result,
                            onDismiss = { viewModel.resetScan() },
                            onUseResult = { data ->
                                navController.navigate("addWine?scannedName=${data.name}&scannedProducer=${data.producer}&scannedVintage=${data.vintage}&scannedType=${data.wineType}")
                            }
                        )
                    }
                    is ScannerViewModel.ScanState.Error -> {
                        AlertDialog(
                            onDismissRequest = { viewModel.resetScan() },
                            title = { Text("Skanning misslyckades") },
                            text = { Text(state.message) },
                            confirmButton = {
                                TextButton(onClick = { viewModel.resetScan() }) {
                                    Text("Försök igen")
                                }
                            }
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun CameraPreview(viewModel: ScannerViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()
                
                viewModel.setImageCapture(imageCapture)
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))
            
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun ScanResultDialog(
    result: ScanResult.Success,
    onDismiss: () -> Unit,
    onUseResult: (ScanResult.Success) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ettikett hittad") },
        text = {
            Column {
                Text("Konfidens: ${(result.confidence * 100).toInt()}%")
                Spacer(modifier = Modifier.height(8.dp))
                
                result.extractedData.name?.let {
                    Text("Namn: $it", style = MaterialTheme.typography.bodyLarge)
                }
                result.extractedData.producer?.let {
                    Text("Producent: $it", style = MaterialTheme.typography.bodyMedium)
                }
                result.extractedData.vintage?.let {
                    Text("Årgång: $it", style = MaterialTheme.typography.bodyMedium)
                }
                result.extractedData.wineType?.let {
                    Text("Typ: ${it.displayName()}", style = MaterialTheme.typography.bodyMedium)
                }
                result.extractedData.country?.let {
                    Text("Land: $it", style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(onClick = { onUseResult(result) }) {
                Text("Använd")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Skanna igen")
            }
        }
    )
}
