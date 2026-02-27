package se.ahlen.vinkallaren.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.util.Date

/**
 * Core Wine entity representing a bottle in the cellar
 */
@Entity(
    tableName = "wines",
    indices = [
        Index(value = ["name"]),
        Index(value = ["producer"]),
        Index(value = ["vintage"]),
        Index(value = ["wineType"]),
        Index(value = ["region"]),
        Index(value = ["isReadyToDrink"])
    ]
)
data class Wine(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Basic info
    val name: String,
    val producer: String,
    val vintage: Int?,
    val wineType: WineType,
    
    // Origin
    val country: String?,
    val region: String?,
    val subRegion: String?,
    val appellation: String?,
    
    // Classification
    val grapeVarieties: String?, // JSON array of grape varieties
    val alcoholContent: Float?,
    val bottleSize: String = "750ml",
    
    // Cellar info
    val quantity: Int = 1,
    val storageLocation: String?,
    val purchasePrice: Float?,
    val purchaseDate: Date?,
    val currency: String = "SEK",
    
    // Photos - stored as file paths
    val frontLabelPhoto: String?,
    val backLabelPhoto: String?,
    val bottlePhoto: String?,
    val wineColorPhoto: String?,
    
    // Quality & drinking
    val personalRating: Float?, // 0-5 scale
    val professionalRating: Float?, // e.g., Wine Spectator, Vivino
    val drinkingWindowStart: Int?, // Year
    val drinkingWindowEnd: Int?, // Year
    val peakMaturityYear: Int?,
    val isReadyToDrink: Boolean = false,
    
    // Tasting notes
    val tastingNotes: String?,
    val foodPairings: String?, // JSON array
    val tastingDate: Date?,
    
    // External IDs
    val vivinoId: String?,
    val systembolagetId: String?,
    val cellarTrackerId: String?,
    val barcode: String?,
    
    // Metadata
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isSynced: Boolean = false,
    val firebaseId: String? = null,
    val isDeleted: Boolean = false
)

enum class WineType {
    RED,
    WHITE,
    ROSE,
    SPARKLING,
    DESSERT,
    FORTIFIED,
    ORANGE,
    UNKNOWN;
    
    fun displayName(): String = when (this) {
        RED -> "Rött vin"
        WHITE -> "Vitt vin"
        ROSE -> "Rosé"
        SPARKLING -> "Mousserande"
        DESSERT -> "Dessertvin"
        FORTIFIED -> "Förstärkt vin"
        ORANGE -> "Orange vin"
        UNKNOWN -> "Okänd"
    }
}

enum class WineColor {
    PURPLE,
    RUBY,
    GARNET,
    TAWNY,
    BROWN,
    LEMON_GREEN,
    LEMON,
    GOLD,
    AMBER,
    BROWN_WHITE;
    
    fun displayName(): String = when (this) {
        PURPLE -> "Lila" // Very young red
        RUBY -> "Rubin" // Young red
        GARNET -> "Granat" // Mature red
        TAWNY -> "Tawny" // Aged red
        BROWN -> "Brun" // Very old red
        LEMON_GREEN -> "Ljungrön" // Very young white
        LEMON -> "Citron" // Young white
        GOLD -> "Guld" // Mature white
        AMBER -> "Bärnsten" // Aged white
        BROWN_WHITE -> "Brun" // Very old white
    }
}

enum class SweetnessLevel {
    DRY,
    OFF_DRY,
    MEDIUM_DRY,
    MEDIUM_SWEET,
    SWEET;
    
    fun displayName(): String = when (this) {
        DRY -> "Torr"
        OFF_DRY -> "Halvtorr"
        MEDIUM_DRY -> "Medelfyllig"
        MEDIUM_SWEET -> "Halvsöt"
        SWEET -> "Söt"
    }
}

enum class BodyLevel {
    LIGHT,
    MEDIUM_MINUS,
    MEDIUM,
    MEDIUM_PLUS,
    FULL;
    
    fun displayName(): String = when (this) {
        LIGHT -> "Lätt"
        MEDIUM_MINUS -> "Medellätt"
        MEDIUM -> "Medium"
        MEDIUM_PLUS -> "Medium+fyllig"
        FULL -> "Fyllig"
    }
}

enum class TanninLevel {
    LOW,
    MEDIUM_MINUS,
    MEDIUM,
    MEDIUM_PLUS,
    HIGH;
    
    fun displayName(): String = when (this) {
        LOW -> "Låg"
        MEDIUM_MINUS -> "Medellåg"
        MEDIUM -> "Medium"
        MEDIUM_PLUS -> "Medium+hög"
        HIGH -> "Hög"
    }
}

enum class AcidityLevel {
    LOW,
    MEDIUM_MINUS,
    MEDIUM,
    MEDIUM_PLUS,
    HIGH;
    
    fun displayName(): String = when (this) {
        LOW -> "Låg"
        MEDIUM_MINUS -> "Medellåg"
        MEDIUM -> "Medium"
        MEDIUM_PLUS -> "Medium+hög"
        HIGH -> "Hög"
    }
}
