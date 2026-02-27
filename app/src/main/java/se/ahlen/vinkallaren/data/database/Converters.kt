package se.ahlen.vinkallaren.data.database

import androidx.room.TypeConverter
import se.ahlen.vinkallaren.data.model.*
import java.util.Date

class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromWineType(value: String?): WineType? {
        return value?.let { WineType.valueOf(it) }
    }
    
    @TypeConverter
    fun wineTypeToString(type: WineType?): String? {
        return type?.name
    }
    
    @TypeConverter
    fun fromLocationType(value: String?): LocationType? {
        return value?.let { LocationType.valueOf(it) }
    }
    
    @TypeConverter
    fun locationTypeToString(type: LocationType?): String? {
        return type?.name
    }
    
    @TypeConverter
    fun fromSweetnessLevel(value: String?): SweetnessLevel? {
        return value?.let { SweetnessLevel.valueOf(it) }
    }
    
    @TypeConverter
    fun sweetnessLevelToString(level: SweetnessLevel?): String? {
        return level?.name
    }
    
    @TypeConverter
    fun fromBodyLevel(value: String?): BodyLevel? {
        return value?.let { BodyLevel.valueOf(it) }
    }
    
    @TypeConverter
    fun bodyLevelToString(level: BodyLevel?): String? {
        return level?.name
    }
    
    @TypeConverter
    fun fromTanninLevel(value: String?): TanninLevel? {
        return value?.let { TanninLevel.valueOf(it) }
    }
    
    @TypeConverter
    fun tanninLevelToString(level: TanninLevel?): String? {
        return level?.name
    }
    
    @TypeConverter
    fun fromAcidityLevel(value: String?): AcidityLevel? {
        return value?.let { AcidityLevel.valueOf(it) }
    }
    
    @TypeConverter
    fun acidityLevelToString(level: AcidityLevel?): String? {
        return level?.name
    }
    
    @TypeConverter
    fun fromClarity(value: String?): Clarity? {
        return value?.let { Clarity.valueOf(it) }
    }
    
    @TypeConverter
    fun clarityToString(clarity: Clarity?): String? {
        return clarity?.name
    }
    
    @TypeConverter
    fun fromIntensity(value: String?): Intensity? {
        return value?.let { Intensity.valueOf(it) }
    }
    
    @TypeConverter
    fun intensityToString(intensity: Intensity?): String? {
        return intensity?.name
    }
    
    @TypeConverter
    fun fromViscosity(value: String?): Viscosity? {
        return value?.let { Viscosity.valueOf(it) }
    }
    
    @TypeConverter
    fun viscosityToString(viscosity: Viscosity?): String? {
        return viscosity?.name
    }
    
    @TypeConverter
    fun fromFinish(value: String?): Finish? {
        return value?.let { Finish.valueOf(it) }
    }
    
    @TypeConverter
    fun finishToString(finish: Finish?): String? {
        return finish?.name
    }
    
    @TypeConverter
    fun fromQualityAssessment(value: String?): QualityAssessment? {
        return value?.let { QualityAssessment.valueOf(it) }
    }
    
    @TypeConverter
    fun qualityAssessmentToString(assessment: QualityAssessment?): String? {
        return assessment?.name
    }
}
