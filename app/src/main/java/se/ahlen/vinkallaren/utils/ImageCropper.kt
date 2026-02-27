package se.ahlen.vinkallaren.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import se.ahlen.vinkallaren.data.remote.BoundingBox
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for cropping images
 * Used for extracting wine labels from bottle photos
 */
@Singleton
class ImageCropper @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "ImageCropper"
        private const val TEMP_FILE_PREFIX = "cropped_label_"
        private const val TEMP_FILE_SUFFIX = ".jpg"
        private const val MAX_CROP_DIMENSION = 2048 // Max dimension for OCR performance
        private const val JPEG_QUALITY = 95
    }
    
    /**
     * Crop a bitmap to the specified bounding box
     * 
     * @param source Original bitmap
     * @param boundingBox Bounding box coordinates (x1, y1, x2, y2)
     * @return Cropped bitmap or null if failed
     */
    fun cropBitmap(source: Bitmap, boundingBox: BoundingBox): Bitmap? {
        return try {
            val x1 = boundingBox.x1.toInt().coerceIn(0, source.width)
            val y1 = boundingBox.y1.toInt().coerceIn(0, source.height)
            val x2 = boundingBox.x2.toInt().coerceIn(0, source.width)
            val y2 = boundingBox.y2.toInt().coerceIn(0, source.height)
            
            val width = x2 - x1
            val height = y2 - y1
            
            if (width <= 0 || height <= 0) {
                Log.w(TAG, "Invalid crop dimensions: ${width}x${height}")
                return null
            }
            
            // Check if we need to scale down for performance
            val scaleFactor = if (width > MAX_CROP_DIMENSION || height > MAX_CROP_DIMENSION) {
                MAX_CROP_DIMENSION.toFloat() / maxOf(width, height)
            } else 1f
            
            // Crop the bitmap
            val cropped = Bitmap.createBitmap(source, x1, y1, width, height)
            
            // Scale if needed
            return if (scaleFactor < 1f) {
                val newWidth = (width * scaleFactor).toInt()
                val newHeight = (height * scaleFactor).toInt()
                val scaled = Bitmap.createScaledBitmap(cropped, newWidth, newHeight, true)
                if (scaled != cropped) {
                    cropped.recycle()
                }
                scaled
            } else {
                cropped
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error cropping bitmap", e)
            null
        }
    }
    
    /**
     * Save bitmap to a temp file for OCR processing
     * 
     * @param bitmap The bitmap to save
     * @return File path or null if failed
     */
    suspend fun saveToTempFile(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        try {
            val tempFile = File.createTempFile(
                TEMP_FILE_PREFIX + System.currentTimeMillis(),
                TEMP_FILE_SUFFIX,
                context.cacheDir
            )
            
            FileOutputStream(tempFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }
            
            Log.d(TAG, "Saved cropped image to: ${tempFile.absolutePath}")
            tempFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Error saving temp file", e)
            null
        }
    }
    
    /**
     * Load bitmap from URI
     */
    fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from URI", e)
            null
        }
    }
    
    /**
     * Rotate bitmap if needed
     */
    fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
    
    /**
     * Clean up old temp files
     */
    suspend fun cleanupOldTempFiles(maxAgeMillis: Long = 24 * 60 * 60 * 1000): Int = 
        withContext(Dispatchers.IO) {
            try {
                val now = System.currentTimeMillis()
                val files = context.cacheDir.listFiles { file ->
                    file.name.startsWith(TEMP_FILE_PREFIX)
                } ?: emptyArray()
                
                var deletedCount = 0
                files.forEach { file ->
                    if (now - file.lastModified() > maxAgeMillis) {
                        if (file.delete()) {
                            deletedCount++
                        }
                    }
                }
                
                Log.d(TAG, "Cleaned up $deletedCount old temp files")
                deletedCount
            } catch (e: Exception) {
                Log.e(TAG, "Error cleaning up temp files", e)
                0
            }
        }
    
    /**
     * Draw bounding box on source bitmap (for debugging)
     */
    fun drawBoundingBox(source: Bitmap, boundingBox: BoundingBox, color: Int = 0xFF00FF00.toInt()): Bitmap {
        val result = source.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            this.color = color
            strokeWidth = 5f
            style = Paint.Style.STROKE
        }
        
        canvas.drawRect(
            boundingBox.x1,
            boundingBox.y1,
            boundingBox.x2,
            boundingBox.y2,
            paint
        )
        
        return result
    }
    
    /**
     * Get aspect ratio preserving dimensions
     */
    fun calculateAspectRatioFit(
        sourceWidth: Int,
        sourceHeight: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Pair<Int, Int> {
        val ratio = minOf(
            maxWidth.toFloat() / sourceWidth,
            maxHeight.toFloat() / sourceHeight
        )
        
        return Pair(
            (sourceWidth * ratio).toInt(),
            (sourceHeight * ratio).toInt()
        )
    }
}
