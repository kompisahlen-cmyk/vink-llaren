package se.ahlen.vinkallaren.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 * Roboflow Wine Label Detection API Service
 * 
 * API Endpoint: https://detect.roboflow.com/wine-label/1
 * FREE TIER: 10,000 calls/month
 */
interface RoboflowApiService {
    
    companion object {
        const val BASE_URL = "https://detect.roboflow.com/"
        const val MODEL_ENDPOINT = "wine-label/1"
    }
    
    /**
     * Detect wine label in an image
     * Uses multipart/form-data to upload the image
     * 
     * @param apiKey Roboflow API key (query parameter)
     * @param image Multipart image file
     * @return LabelDetectionResponse with bounding box predictions
     */
    @Multipart
    @POST("wine-label/1")
    suspend fun detectLabel(
        @Query("api_key") apiKey: String,
        @Part image: MultipartBody.Part
    ): Response<LabelDetectionResponse>
    
    /**
     * Detect wine label using base64 encoded image
     * Alternative method if multipart doesn't work well
     * 
     * @param apiKey Roboflow API key
     * @param imageBase64 Base64 encoded image string
     * @return LabelDetectionResponse
     */
    @GET("wine-label/1")
    suspend fun detectLabelBase64(
        @Query("api_key") apiKey: String,
        @Query("image") imageBase64: String
    ): Response<LabelDetectionResponse>
}

/**
 * Systembolaget API Service
 * Public API for searching products
 */
interface SystembolagetApiService {
    
    companion object {
        const val BASE_URL = "https://api.systembolaget.se/api/"
    }
    
    /**
     * Search for products by name
     * Note: This is a simplified interface - actual Systembolaget API
     * may require different authentication
     * 
     * @param query Search query
     * @param page Page number
     * @param pageSize Results per page
     */
    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): Response<SystembolagetSearchResponse>
}
