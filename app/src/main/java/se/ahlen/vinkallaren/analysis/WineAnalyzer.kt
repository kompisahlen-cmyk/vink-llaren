package se.ahlen.vinkallaren.analysis

import se.ahlen.vinkallaren.data.model.Wine
import se.ahlen.vinkallaren.data.model.WineType
import java.util.Calendar

/**
 * Wine analysis utilities for drinking windows, food pairing, and maturity estimation
 */
object WineAnalyzer {
    
    /**
     * Get current year
     */
    fun getCurrentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)
    
    /**
     * Calculate drinking window based on wine type, vintage, region and grape
     */
    fun calculateDrinkingWindow(
        wineType: WineType,
        vintage: Int?,
        region: String? = null,
        grapeVarieties: List<String>? = null
    ): DrinkingWindow {
        val year = vintage ?: getCurrentYear()
        val currentYear = getCurrentYear()
        val age = currentYear - year
        
        return when (wineType) {
            WineType.RED -> calculateRedWineWindow(year, age, region, grapeVarieties)
            WineType.WHITE -> calculateWhiteWineWindow(year, age, region, grapeVarieties)
            WineType.ROSE -> DrinkingWindow(
                start = year + 1,
                peak = minOf(year + 2, currentYear + 1),
                end = year + 3,
                notes = "Rosé dricks bäst ungt, inom 1-3 år från skörden"
            )
            WineType.SPARKLING -> calculateSparklingWindow(year, age, region)
            WineType.DESSERT -> calculateDessertWindow(year, age, region)
            WineType.FORTIFIED -> DrinkingWindow(
                start = year,
                peak = year + 10,
                end = year + 50,
                notes = "Förstärkta viner kan lagras mycket länge"
            )
            WineType.ORANGE -> DrinkingWindow(
                start = year + 2,
                peak = year + 5,
                end = year + 10,
                notes = "Orange viner utvecklas unikt med lagring"
            )
            else -> DrinkingWindow(
                start = year,
                peak = year + 3,
                end = year + 5,
                notes = "Generisk drickfönster"
            )
        }
    }
    
    private fun calculateRedWineWindow(
        vintage: Int,
        age: Int,
        region: String?,
        grapes: List<String>?
    ): DrinkingWindow {
        // Grape-based rules
        val grape = grapes?.firstOrNull()?.lowercase() ?: ""
        
        return when {
            // Cabernet Sauvignon based
            "cabernet sauvignon" in grape -> DrinkingWindow(
                start = vintage + 5,
                peak = vintage + 10,
                end = vintage + 20,
                notes = "Cabernet Sauvignon utvecklas långsamt, full mognad efter 8-15 år"
            )
            
            // Pinot Noir
            "pinot noir" in grape || region?.contains("Burgundy", ignoreCase = true) == true -> DrinkingWindow(
                start = vintage + 3,
                peak = vintage + 8,
                end = vintage + 15,
                notes = "Pinot Noir når tidigare mognad, drick 5-12 år"
            )
            
            // Nebbiolo (Barolo/Barbaresco)
            "nebbiolo" in grape -> DrinkingWindow(
                start = vintage + 7,
                peak = vintage + 15,
                end = vintage + 25,
                notes = "Nebbiolo kräver lång lagring, full mognad efter 10-20 år"
            )
            
            // Tempranillo (Rioja/Ribera del Duero)
            "tempranillo" in grape -> DrinkingWindow(
                start = vintage + 4,
                peak = vintage + 8,
                end = vintage + 15,
                notes = "Tempranillo: Reserva 5-10 år, Gran Reserva 10-20 år"
            )
            
            // Syrah/Shiraz
            "syrah" in grape || "shiraz" in grape -> DrinkingWindow(
                start = vintage + 4,
                peak = vintage + 8,
                end = vintage + 15,
                notes = "Syrah/Shiraz: 5-10 år för optimal mognad"
            )
            
            // Merlot
            "merlot" in grape -> DrinkingWindow(
                start = vintage + 3,
                peak = vintage + 7,
                end = vintage + 12,
                notes = "Merlot mognar tidigare än Cabernet"
            )
            
            // Sangiovese (Chianti, Brunello)
            "sangiovese" in grape -> DrinkingWindow(
                start = vintage + 3,
                peak = vintage + 7,
                end = vintage + 15,
                notes = "Sangiovese: Chianti 3-8 år, Brunello 5-15 år"
            )
            
            // Grenache
            "grenache" in grape -> DrinkingWindow(
                start = vintage + 2,
                peak = vintage + 5,
                end = vintage + 10,
                notes = "Grenache dricks bäst relativt ungt"
            )
            
            // Light reds
            "gamay" in grape || "beaujolais" in region?.lowercase() ?: "" -> DrinkingWindow(
                start = vintage + 1,
                peak = vintage + 2,
                end = vintage + 5,
                notes = "Lätta rödviner dricks bäst unga"
            )
            
            // Bordeaux blends (default)
            region?.contains("Bordeaux", ignoreCase = true) == true -> DrinkingWindow(
                start = vintage + 5,
                peak = vintage + 12,
                end = vintage + 25,
                notes = "Bordeaux: Cru Classé 10-30 år, vanlig 5-10 år"
            )
            
            // Default for red
            else -> DrinkingWindow(
                start = vintage + 3,
                peak = vintage + 6,
                end = vintage + 10,
                notes = "De flesta rödviner är optimala efter 3-8 år"
            )
        }
    }
    
    private fun calculateWhiteWineWindow(
        vintage: Int,
        age: Int,
        region: String?,
        grapes: List<String>?
    ): DrinkingWindow {
        val grape = grapes?.firstOrNull()?.lowercase() ?: ""
        
        return when {
            // Chardonnay - depends on oaking
            "chardonnay" in grape -> {
                if (region?.contains("Burgundy", ignoreCase = true) == true ||
                    region?.contains("Côte d'Or", ignoreCase = true) == true) {
                    DrinkingWindow(
                        start = vintage + 3,
                        peak = vintage + 7,
                        end = vintage + 15,
                        notes = "Burgundy Chardonnay: Montrachet 5-15 år, Village 3-8 år"
                    )
                } else {
                    DrinkingWindow(
                        start = vintage + 1,
                        peak = vintage + 3,
                        end = vintage + 6,
                        notes = "Chardonnay: drick ungt, upp till 5 år"
                    )
                }
            }
            
            // Riesling - great aging potential
            "riesling" in grape -> {
                if (region?.contains("Mosel", ignoreCase = true) == true ||
                    region?.contains("Rheingau", ignoreCase = true) == true) {
                    DrinkingWindow(
                        start = vintage + 2,
                        peak = vintage + 8,
                        end = vintage + 20,
                        notes = "Tysk Riesling: halbtrocken 5-15 år, trocken 3-10 år, söt 10-30 år"
                    )
                } else {
                    DrinkingWindow(
                        start = vintage + 2,
                        peak = vintage + 5,
                        end = vintage + 10,
                        notes = "Riesling åldras elegant, särskilt från kalla klimat"
                    )
                }
            }
            
            // Sauvignon Blanc - drink young
            "sauvignon blanc" in grape -> DrinkingWindow(
                start = vintage,
                peak = vintage + 1,
                end = vintage + 3,
                notes = "Sauvignon Blanc dricks bäst inom 1-2 år"
            )
            
            // Chenin Blanc - excellent aging
            "chenin blanc" in grape || region?.contains("Loire", ignoreCase = true) == true -> DrinkingWindow(
                start = vintage + 2,
                peak = vintage + 8,
                end = vintage + 15,
                notes = "Chenin Blanc från Loire kan lagras 10-20 år"
            )
            
            // Grüner Veltliner
            "grüner veltliner" in grape -> DrinkingWindow(
                start = vintage + 2,
                peak = vintage + 4,
                end = vintage + 8,
                notes = "Grüner Veltliner: 2-5 år för Smaragd, 1-3 för klassisk"
            )
            
            // Gewürztraminer
            "gewürztraminer" in grape || "gewurztraminer" in grape -> DrinkingWindow(
                start = vintage + 1,
                peak = vintage + 3,
                end = vintage + 6,
                notes = "Gewürztraminer dricks bäst relativt ungt"
            )
            
            // Viognier
            "viognier" in grape -> DrinkingWindow(
                start = vintage + 2,
                peak = vintage + 4,
                end = vintage + 7,
                notes = "Viognier utvecklas till 3-5 år"
            )
            
            // Default for white
            else -> DrinkingWindow(
                start = vintage,
                peak = vintage + 2,
                end = vintage + 4,
                notes = "De flesta vita viner dricks bäst inom 1-3 år"
            )
        }
    }
    
    private fun calculateSparklingWindow(
        vintage: Int,
        age: Int,
        region: String?
    ): DrinkingWindow {
        return when {
            region?.contains("Champagne", ignoreCase = true) == true -> {
                if (vintage > 1990) { // Assuming vintage Champagne
                    DrinkingWindow(
                        start = vintage + 5,
                        peak = vintage + 10,
                        end = vintage + 20,
                        notes = "Vintage Champagne: 5-20 år,non-vintage 2-3 år"
                    )
                } else {
                    DrinkingWindow(
                        start = vintage,
                        peak = vintage + 2,
                        end = vintage + 4,
                        notes = "Non-vintage Champagne: drick inom 2-3 år"
                    )
                }
            }
            region?.contains("Cava", ignoreCase = true) == true -> DrinkingWindow(
                start = vintage,
                peak = vintage + 2,
                end = vintage + 5,
                notes = "Cava: Reserva 2-3 år, Gran Reserva 3-5 år"
            )
            else -> DrinkingWindow(
                start = vintage,
                peak = vintage + 1,
                end = vintage + 3,
                notes = "Mousserande vin dricks bäst ungt"
            )
        }
    }
    
    private fun calculateDessertWindow(
        vintage: Int,
        age: Int,
        region: String?
    ): DrinkingWindow {
        return when {
            region?.contains("Sauternes", ignoreCase = true) == true ||
            region?.contains("Barsac", ignoreCase = true) == true -> DrinkingWindow(
                start = vintage + 5,
                peak = vintage + 15,
                end = vintage + 30,
                notes = "Sauternes: exceptionell åldringspotential 10-30 år"
            )
            else -> DrinkingWindow(
                start = vintage + 2,
                peak = vintage + 8,
                end = vintage + 15,
                notes = "Dessertviner lagras generellt längre"
            )
        }
    }
    
    /**
     * Check if a wine is currently ready to drink
     */
    fun isReadyToDrink(wine: Wine): Boolean {
        val currentYear = getCurrentYear()
        val windowStart = wine.drinkingWindowStart ?: return false
        val windowEnd = wine.drinkingWindowEnd ?: Int.MAX_VALUE
        
        return currentYear in windowStart..windowEnd
    }
    
    /**
     * Get drinking status for display
     */
    fun getDrinkingStatus(wine: Wine): DrinkingStatus {
        val currentYear = getCurrentYear()
        val windowStart = wine.drinkingWindowStart
        val windowEnd = wine.drinkingWindowEnd
        
        return when {
            windowStart == null || windowEnd == null -> DrinkingStatus.UNKNOWN
            currentYear > windowEnd -> DrinkingStatus.OVERDUE
            currentYear >= windowStart -> DrinkingStatus.READY
            currentYear >= windowStart - 1 -> DrinkingStatus.APPROACHING
            else -> DrinkingStatus.TOO_YOUNG
        }
    }
    
    /**
     * Get food pairing suggestions based on wine characteristics
     */
    fun getFoodPairings(
        wineType: WineType,
        region: String? = null,
        grapes: List<String>? = null
    ): List<FoodPairing> {
        return FoodPairingEngine.getPairings(wineType, region, grapes)
    }
    
    /**
     * Calculate value score (price to quality ratio)
     */
    fun calculateValueScore(rating: Float?, price: Float?): Float? {
        if (rating == null || price == null || price <= 0) return null
        
        // QPR (Quality Price Ratio) calculation
        return (rating * rating) / price * 10
    }
}

data class DrinkingWindow(
    val start: Int,
    val peak: Int,
    val end: Int,
    val notes: String
) {
    fun displayString(): String = "$start - $end (topp: $peak)"
    
    fun yearsUntilDrinkable(currentYear: Int): Int = maxOf(0, start - currentYear)
    
    fun yearsUntilPeak(currentYear: Int): Int = maxOf(0, peak - currentYear)
}

enum class DrinkingStatus {
    READY,
    APPROACHING,
    TOO_YOUNG,
    OVERDUE,
    UNKNOWN;
    
    fun displayName(): String = when (this) {
        READY -> "Redo att dricka"
        APPROACHING -> "Nästan redo"
        TOO_YOUNG -> "För ung"
        OVERDUE -> "Drick nu!"
        UNKNOWN -> "Okänd"
    }
    
    fun colorHex(): String = when (this) {
        READY -> "#4CAF50" // Green
        APPROACHING -> "#FFC107" // Amber
        TOO_YOUNG -> "#2196F3" // Blue
        OVERDUE -> "#F44336" // Red
        UNKNOWN -> "#9E9E9E" // Gray
    }
}
