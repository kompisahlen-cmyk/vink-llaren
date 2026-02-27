package se.ahlen.vinkallaren.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Tasting note for a specific wine experience
 */
@Entity(
    tableName = "tasting_notes",
    foreignKeys = [
        ForeignKey(
            entity = Wine::class,
            parentColumns = ["id"],
            childColumns = ["wineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["wineId"])
    ]
)
data class TastingNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val wineId: Long,
    
    // When & where
    val tastingDate: Date = Date(),
    val location: String?,
    val occasion: String?, // e.g., "Dinner party", "Anniversary"
    
    // Visual
    val color: String?, // e.g., "Deep ruby with garnet rim"
    val clarity: Clarity?,
    val intensity: Intensity?,
    val viscosity: Viscosity?,
    
    // Aroma
    val noseIntensity: Intensity?,
    val primaryAromas: String?, // Fruit, floral, herbal
    val secondaryAromas: String?, // Yeast, MLF, oak
    val tertiaryAromas: String?, // Age-related
    
    // Palate
    val sweetness: SweetnessLevel?,
    val acidity: AcidityLevel?,
    val tannin: TanninLevel?,
    val alcohol: Float?,
    val body: BodyLevel?,
    val flavorIntensity: Intensity?,
    val flavorCharacteristics: String?, // Description
    val finish: Finish?,
    
    // Conclusion
    val quality: QualityAssessment?,
    val drinkingWindow: String?, // e.g., "Drink now - 2030"
    val foodPairings: String?, // JSON array
    val score: Float?, // Personal score, e.g., 92/100 or 4.5/5
    val notes: String?, // Free text
    
    // Metadata
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isSynced: Boolean = false
)

enum class Clarity {
    CRYSTAL_CLEAR,
    CLEAR,
    SLIGHTLY_HAZY,
    HAZY;
    
    fun displayName(): String = when (this) {
        CRYSTAL_CLEAR -> "Kristallklar"
        CLEAR -> "Klar"
        SLIGHTLY_HAZY -> "Lätt disig"
        HAZY -> "Disig"
    }
}

enum class Intensity {
    PALE,
    MEDIUM_MINUS,
    MEDIUM,
    MEDIUM_PLUS,
    DEEP;
    
    fun displayName(): String = when (this) {
        PALE -> "Svag"
        MEDIUM_MINUS -> "Medelsvag"
        MEDIUM -> "Medium"
        MEDIUM_PLUS -> "Medium+kraftig"
        DEEP -> "Kraftig"
    }
}

enum class Viscosity {
    LOW,
    MEDIUM,
    HIGH;
    
    fun displayName(): String = when (this) {
        LOW -> "Lätt"
        MEDIUM -> "Medium"
        HIGH -> "Kraftig"
    }
}

enum class Finish {
    SHORT,
    MEDIUM_MINUS,
    MEDIUM,
    MEDIUM_PLUS,
    LONG;
    
    fun displayName(): String = when (this) {
        SHORT -> "Kort"
        MEDIUM_MINUS -> "Medelkort"
        MEDIUM -> "Medium"
        MEDIUM_PLUS -> "Medellång"
        LONG -> "Lång"
    }
}

enum class QualityAssessment {
    FAULTY,
    POOR,
    ACCEPTABLE,
    GOOD,
    VERY_GOOD,
    OUTSTANDING;
    
    fun displayName(): String = when (this) {
        FAULTY -> "Felaktig"
        POOR -> "Dålig"
        ACCEPTABLE -> "Acceptabel"
        GOOD -> "Bra"
        VERY_GOOD -> "Mycket bra"
        OUTSTANDING -> "Enastående"
    }
}
