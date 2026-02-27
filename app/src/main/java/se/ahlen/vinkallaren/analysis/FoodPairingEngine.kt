package se.ahlen.vinkallaren.analysis

import se.ahlen.vinkallaren.data.model.WineType

/**
 * Food pairing suggestions based on wine characteristics
 */
object FoodPairingEngine {
    
    fun getPairings(
        wineType: WineType,
        region: String?,
        grapes: List<String>?
    ): List<FoodPairing> {
        return when (wineType) {
            WineType.RED -> getRedWinePairings(region, grapes)
            WineType.WHITE -> getWhiteWinePairings(region, grapes)
            WineType.ROSE -> getRosePairings()
            WineType.SPARKLING -> getSparklingPairings(region)
            WineType.DESSERT -> getDessertPairings(region)
            WineType.FORTIFIED -> getFortifiedPairings()
            WineType.ORANGE -> getOrangePairings()
            else -> getGenericPairings()
        }
    }
    
    private fun getRedWinePairings(region: String?, grapes: List<String>?): List<FoodPairing> {
        val grape = grapes?.firstOrNull()?.lowercase() ?: ""
        return listOf(
            FoodPairing("Grillat", "Rött kött från grillen", PairingQuality.EXCELLENT),
            FoodPairing("Chark", "Skinka, korv", PairingQuality.GOOD)
        )
    }
    
    private fun getWhiteWinePairings(region: String?, grapes: List<String>?): List<FoodPairing> {
        return listOf(
            FoodPairing("Fisk", "Vit fisk, skaldjur", PairingQuality.EXCELLENT),
            FoodPairing("Kyckling", "Lätare rätter", PairingQuality.GOOD)
        )
    }
    
    private fun getRosePairings(): List<FoodPairing> = listOf(
        FoodPairing("Sallad", "Sommarsallad", PairingQuality.EXCELLENT),
        FoodPairing("Tapas", "Spanska tilltugg", PairingQuality.VERY_GOOD)
    )
    
    private fun getSparklingPairings(region: String?): List<FoodPairing> = listOf(
        FoodPairing("Ostron", "Färska ostron", PairingQuality.EXCELLENT),
        FoodPairing("Förrätt", "Tapas, tilltugg", PairingQuality.EXCELLENT),
        FoodPairing("Chark", "Charkuterier", PairingQuality.GOOD)
    )
    
    private fun getDessertPairings(region: String?): List<FoodPairing> = listOf(
        FoodPairing("Dessert", "Choklad, bär", PairingQuality.EXCELLENT),
        FoodPairing("Ost", "Blåmögel", PairingQuality.EXCELLENT)
    )
    
    private fun getFortifiedPairings(): List<FoodPairing> = listOf(
        FoodPairing("Nötter", "Mandlar, valnötter", PairingQuality.EXCELLENT),
        FoodPairing("Dessert", "Choklad", PairingQuality.GOOD)
    )
    
    private fun getOrangePairings(): List<FoodPairing> = listOf(
        FoodPairing("Asiatiskt", "Kryddig mat", PairingQuality.EXCELLENT),
        FoodPairing("Fisk", "Fet fisk", PairingQuality.GOOD)
    )
    
    private fun getGenericPairings(): List<FoodPairing> = listOf(
        FoodPairing("Mat", "Passar till mat", PairingQuality.GOOD)
    )
}

data class FoodPairing(
    val category: String,
    val examples: String,
    val quality: PairingQuality
)

enum class PairingQuality {
    EXCELLENT,
    VERY_GOOD,
    GOOD,
    FAIR;
    
    fun displayName(): String = when (this) {
        EXCELLENT -> "Perfekt"
        VERY_GOOD -> "Mycket bra"
        GOOD -> "Bra"
        FAIR -> "OK"
    }
    
    fun stars(): String = when (this) {
        EXCELLENT -> "★★★★★"
        VERY_GOOD -> "★★★★☆"
        GOOD -> "★★★☆☆"
        FAIR -> "★★☆☆☆"
    }
}
