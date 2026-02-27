package se.ahlen.vinkallaren.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import se.ahlen.vinkallaren.BuildConfig

/**
 * Data source for Roboflow Wine Label Detection API
 * 
 * FREE TIER LIMITS:
 * - 10,000 API calls per month
 * - Sufficient for personal wine cellar app usage
 */
@Singleton
class RoboflowDataSource @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "RoboflowDataSource"
        private const val CONFIDENCE_THRESHOLD = 0.5f
        private const val JPEG_QUALITY = 90
    }
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(RoboflowApiService.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val apiService: RoboflowApiService = retrofit.create(RoboflowApiService::class.java)
    
    /**
     * Get API key from build config or local.properties
     * FALLBACK: Empty string - app will skip detection if not configured
     */
    private val apiKey: String
        get() = BuildConfig.ROBOFLOW_API_KEY ?: ""
    
    val isConfigured: Boolean
        get() = apiKey.isNotBlank()
    
    /**
     * Detect wine label in a bitmap image
     * 
     * @param bitmap The image to analyze
     * @return LabelDetectionResponse with predictions
     */
    suspend fun detectLabel(bitmap: Bitmap): Result<LabelDetectionResponse> = 
        withContext(Dispatchers.IO) {
            if (!isConfigured) {
                return@withContext Result.failure(
                    IllegalStateException("Roboflow API key not configured")
                )
            }
            
            try {
                // Convert bitmap to JPEG bytes
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
                val imageBytes = stream.toByteArray()
                
                // Create multipart request
                val requestBody = imageBytes.toRequestBody(
                    "image/jpeg".toMediaTypeOrNull()
                )
                val part = MultipartBody.Part.createFormData(
                    "file",
                    "wine_label.jpg",
                    requestBody
                )
                
                Log.d(TAG, "Sending detection request to Roboflow...")
                val response = apiService.detectLabel(apiKey, part)
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        Log.d(TAG, "Detection successful: ${body.predictions.size} predictions")
                        Result.success(body)
                    } else {
                        Result.failure(IOException("Empty response body"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e(TAG, "Detection failed: $errorBody")
                    Result.failure(
                        IOException("API error: ${response.code()} - $errorBody")
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Detection error", e)
                Result.failure(e)
            }
        }
    
    /**
     * Get the best label prediction from response
     * Returns the prediction with highest confidence above threshold
     * 
     * @param response The detection response
     * @return Best prediction or null if none found
     */
    fun getBestPrediction(response: LabelDetectionResponse): LabelPrediction? {
        return response.predictions
            .filter { it.confidence >= CONFIDENCE_THRESHOLD }
            .maxByOrNull { it.confidence }
    }
}
