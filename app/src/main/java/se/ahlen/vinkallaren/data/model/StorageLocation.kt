package se.ahlen.vinkallaren.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Storage location for wines (e.g., wine fridge, cellar, cabinet)
 */
@Entity(
    tableName = "storage_locations",
    indices = [
        Index(value = ["name"], unique = true)
    ]
)
data class StorageLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String, // e.g., "Vinkylen", "Vinkällaren", "Skåpet i köket"
    val description: String?,
    val type: LocationType,
    val capacity: Int?, // Number of bottles it can hold
    val currentCount: Int = 0,
    val temperatureCelsius: Float?, // Target temperature
    val humidityPercent: Float?, // Target humidity
    val isActive: Boolean = true
)

enum class LocationType {
    WINE_FRIDGE,
    CELLAR,
    CABINET,
    RACK,
    OTHER;
    
    fun displayName(): String = when (this) {
        WINE_FRIDGE -> "Vinkyl"
        CELLAR -> "Vinkällare"
        CABINET -> "Skåp"
        RACK -> "Vinställ"
        OTHER -> "Annan"
    }
    
    fun iconName(): String = when (this) {
        WINE_FRIDGE -> "ac_unit"
        CELLAR -> "warehouse"
        CABINET -> "kitchen"
        RACK -> "shelves"
        OTHER -> "inventory_2"
    }
}
