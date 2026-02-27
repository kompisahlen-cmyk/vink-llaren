package se.ahlen.vinkallaren.scanner

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import se.ahlen.vinkallaren.data.model.WineType
import java.util.regex.Pattern

class WineScanner(private val context: Context) {
    
    private val textRecognizer: TextRecognizer = 
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    suspend fun scanLabel(bitmap: Bitmap): ScanResult = withContext(Dispatchers.Default) {
        val image = InputImage.fromBitmap(bitmap, 0)
        try {
            val result = textRecognizer.process(image).await()
            processTextResult(result)
        } catch (e: Exception) {
            ScanResult.Error("OCR failed: ${e.message}")
        }
    }
    
    suspend fun scanLabel(uri: Uri): ScanResult = withContext(Dispatchers.Default) {
        try {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
            scanLabel(bitmap)
        } catch (e: Exception) {
            ScanResult.Error("Failed to load image: ${e.message}")
        }
    }
    
    private fun processTextResult(text: Text): ScanResult {
        val fullText = text.text
        
        if (fullText.isBlank()) {
            return ScanResult.Error("No text found")
        }
        
        val lines = text.textBlocks.flatMap { it.lines }.map { it.text }
        val combinedText = lines.joinToString(" ")
        
        val wineName = extractWineName(lines, combinedText)
        val producer = extractProducer(lines, combinedText)
        val vintage = extractVintage(fullText)
        val wineType = detectWineType(fullText)
        val country = extractCountry(fullText)
        val region = extractRegion(fullText)
        val alcohol = extractAlcohol(fullText)
        
        val confidence = calculateConfidence(wineName, producer, vintage, wineType, country)
        
        return ScanResult.Success(
            rawText = fullText,
            extractedData = ExtractedWineData(
                name = wineName,
                producer = producer,
                vintage = vintage,
                wineType = wineType,
                country = country,
                region = region,
                alcoholContent = alcohol
            ),
            confidence = confidence
        )
    }
    
    private fun extractWineName(lines: List<String>, combinedText: String): String? {
        val filteredLines = lines.filter { line ->
            val lower = line.lowercase()
            !lower.contains("vol") && !lower.contains("product of") &&
            !lower.contains("bottled") && !lower.contains("contains") &&
            line.length in 4..80
        }
        
        return filteredLines.filter { it.length > 8 }.maxByOrNull { it.length }?.trim()
    }
    
    private fun extractProducer(lines: List<String>, combinedText: String): String? {
        return lines.find { 
            val lower = it.lowercase()
            lower.contains("estate") || lower.contains("winery") || 
            lower.contains("vineyards")
        }?.trim() ?: lines.firstOrNull()?.take(40)
    }
    
    private fun extractVintage(text: String): Int? {
        val pattern = Pattern.compile("\\b(19|20)\\d{2}\\b")
        val matcher = pattern.matcher(text)
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        
        while (matcher.find()) {
            val year = matcher.group().toIntOrNull()
            if (year != null && year in 1900..currentYear) {
                return year
            }
        }
        return null
    }
    
    private fun detectWineType(text: String): WineType? {
        val lower = text.lowercase()
        return when {
            "rosé" in lower || "rose" in lower -> WineType.ROSE
            "rött" in lower || "red" in lower || "rotwein" in lower || "rosso" in lower -> WineType.RED
            "vitt" in lower || "white" in lower || "weiss" in lower || "bianco" in lower -> WineType.WHITE
            "mousserande" in lower || "sparkling" in lower || "champagne" in lower || "cava" in lower || "prosecco" in lower -> WineType.SPARKLING
            "dessert" in lower || "sweet" in lower || "sött" in lower -> WineType.DESSERT
            "fortified" in lower || "port" in lower || "sherry" in lower -> WineType.FORTIFIED
            "orange" in lower -> WineType.ORANGE
            else -> null
        }
    }
    
    private fun extractCountry(text: String): String? {
        val countries = listOf(
            "france" to "Frankrike", "italy" to "Italien", "spain" to "Spanien",
            "portugal" to "Portugal", "germany" to "Tyskland", "austria" to "Österrike",
            "usa" to "USA", "california" to "USA", "napa" to "USA",
            "australia" to "Australien", "argentina" to "Argentina", "chile" to "Chile",
            "south africa" to "Sydafrika", "new zealand" to "Nya Zeeland",
            "hungary" to "Ungern", "croatia" to "Kroatien", "greece" to "Grekland"
        )
        
        val lower = text.lowercase()
        for ((key, value) in countries) {
            if (key in lower || "product of $key" in lower) {
                return value
            }
        }
        return null
    }
    
    private fun extractRegion(text: String): String? {
        val regions = listOf(
            "bordeaux", "burgundy", "champagne", "rhône", "loire", "alsace", "provence",
            "tuscany", "piedmont", "veneto", "chianti", "barolo",
            "rioja", "ribera del duero", "priorat", "cava",
            "douro", "porto",
            "mosel", "rheingau", "pfalz"
        )
        
        val lower = text.lowercase()
        return regions.find { it in lower }?.replaceFirstChar { it.uppercase() }
    }
    
    private fun extractAlcohol(text: String): Float? {
        val patterns = listOf(
            Pattern.compile("(\\d{1,2}[.,]\\d)\\s*%?\\s*vol", Pattern.CASE_INSENSITIVE),
            Pattern.compile("alc\\.?\\s*(\\d{1,2}[.,]\\d)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(\\d{1,2}[.,]\\d)\\s*%")
        )
        
        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                return matcher.group(1)?.replace(",", ".")?.toFloatOrNull()
            }
        }
        return null
    }
    
    private fun calculateConfidence(
        name: String?, producer: String?, vintage: Int?, 
        type: WineType?, country: String?
    ): Float {
        var score = 0f
        var maxScore = 0f
        
        if (!name.isNullOrBlank()) { score += 30; maxScore += 30 }
        if (!producer.isNullOrBlank()) { score += 25; maxScore += 25 }
        if (vintage != null) { score += 20; maxScore += 20 }
        if (type != null) { score += 15; maxScore += 15 }
        if (!country.isNullOrBlank()) { score += 10; maxScore += 10 }
        
        return if (maxScore > 0) score / maxScore else 0f
    }
}

sealed class ScanResult {
    data class Success(
        val rawText: String,
        val extractedData: ExtractedWineData,
        val confidence: Float
    ) : ScanResult()
    
    data class Error(val message: String) : ScanResult()
}

data class ExtractedWineData(
    val name: String?,
    val producer: String?,
    val vintage: Int?,
    val wineType: WineType?,
    val country: String?,
    val region: String?,
    val alcoholContent: Float?
) {
    val isComplete: Boolean
        get() = !name.isNullOrBlank() && !producer.isNullOrBlank()
    
    fun hasMinimumData(): Boolean {
        return !name.isNullOrBlank() || !producer.isNullOrBlank() || vintage != null
    }
}
