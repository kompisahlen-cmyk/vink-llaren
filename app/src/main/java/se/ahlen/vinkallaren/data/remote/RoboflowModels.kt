package se.ahlen.vinkallaren.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Roboflow Wine Label Detection API Response Models
 */

data class LabelDetectionResponse(
    @SerializedName("predictions") val predictions: List<LabelPrediction> = emptyList(),
    @SerializedName("image") val image: ImageInfo? = null
)

data class LabelPrediction(
    @SerializedName("x") val x: Float,
    @SerializedName("y") val y: Float,
    @SerializedName("width") val width: Float,
    @SerializedName("height") val height: Float,
    @SerializedName("confidence") val confidence: Float,
    @SerializedName("class") val className: String,
    @SerializedName("class_id") val classId: Int? = null,
    @SerializedName("detection_id") val detectionId: String? = null
) {
    /**
     * Convert center-based coordinates to bounding box (x1, y1, x2, y2)
     */
    fun toBoundingBox(): BoundingBox {
        val halfWidth = width / 2
        val halfHeight = height / 2
        return BoundingBox(
            x1 = x - halfWidth,
            y1 = y - halfHeight,
            x2 = x + halfWidth,
            y2 = y + halfHeight
        )
    }
}

data class ImageInfo(
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int
)

data class BoundingBox(
    val x1: Float,
    val y1: Float,
    val x2: Float,
    val y2: Float
) {
    fun isValid(): Boolean = x1 >= 0 && y1 >= 0 && x2 > x1 && y2 > y1
    
    fun toIntArray(): IntArray = intArrayOf(
        x1.toInt().coerceAtLeast(0),
        y1.toInt().coerceAtLeast(0),
        x2.toInt(),
        y2.toInt()
    )
    
    /**
     * Clamp bounding box to image dimensions
     */
    fun clampToImage(imageWidth: Int, imageHeight: Int): BoundingBox {
        return BoundingBox(
            x1 = x1.coerceIn(0f, imageWidth.toFloat()),
            y1 = y1.coerceIn(0f, imageHeight.toFloat()),
            x2 = x2.coerceIn(0f, imageWidth.toFloat()),
            y2 = y2.coerceIn(0f, imageHeight.toFloat())
        )
    }
}

/**
 * Result of label detection with the cropped image path
 */
data class LabelDetectionResult(
    val prediction: LabelPrediction?,
    val croppedImagePath: String?,
    val originalWidth: Int,
    val originalHeight: Int
) {
    val hasDetection: Boolean
        get() = prediction != null && prediction.confidence > 0.5f
    
    val confidence: Float
        get() = prediction?.confidence ?: 0f
}

/**
 * Systembolaget API Models (for searching)
 */
data class SystembolagetSearchResponse(
    @SerializedName("products") val products: List<SBProduct> = emptyList(),
    @SerializedName("totalCount") val totalCount: Int = 0
)

data class SBProduct(
    @SerializedName("productId") val productId: String,
    @SerializedName("productName") val productName: String,
    @SerializedName("producerName") val producerName: String,
    @SerializedName("vintage") val vintage: Int?,
    @SerializedName("totalVolume") val totalVolume: Int?,
    @SerializedName("alcoholPercentage") val alcoholPercentage: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("categoryLevel1") val categoryLevel1: String?,
    @SerializedName("categoryLevel2") val categoryLevel2: String?,
    @SerializedName("categoryLevel3") val categoryLevel3: String?,
    @SerializedName("tasteDescription") val tasteDescription: String?,
    @SerializedName("usage") val usage: String?,  // Food pairing info
    @SerializedName("tasteSymbols") val tasteSymbols: List<String>?
)
