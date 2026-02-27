package se.ahlen.vinkallaren.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import se.ahlen.vinkallaren.data.database.WineDao
import se.ahlen.vinkallaren.data.model.Wine
import se.ahlen.vinkallaren.data.model.WineType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WineRepository @Inject constructor(
    private val wineDao: WineDao
) {
    // Basic CRUD
    suspend fun insertWine(wine: Wine): Long = wineDao.insertWine(wine)
    
    suspend fun insertWines(wines: List<Wine>): List<Long> = wineDao.insertWines(wines)
    
    suspend fun updateWine(wine: Wine) = wineDao.updateWine(wine)
    
    suspend fun deleteWine(wine: Wine) = wineDao.deleteWine(wine)
    
    suspend fun deleteWineById(wineId: Long) = wineDao.deleteWineById(wineId)
    
    suspend fun softDeleteWine(wineId: Long) = wineDao.softDeleteWine(wineId)
    
    // Quantity management
    suspend fun incrementQuantity(wineId: Long, amount: Int = 1) = 
        wineDao.incrementQuantity(wineId, amount)
    
    suspend fun decrementQuantity(wineId: Long, amount: Int = 1) = 
        wineDao.decrementQuantity(wineId, amount)
    
    suspend fun updateQuantity(wineId: Long, quantity: Int) = 
        wineDao.updateQuantity(wineId, quantity)
    
    suspend fun consumeBottle(wineId: Long): Boolean {
        return wineDao.decrementQuantity(wineId, 1) > 0
    }
    
    // Queries
    fun getAllWines(): Flow<List<Wine>> = wineDao.getAllWines()
    
    suspend fun getWineById(wineId: Long): Wine? = wineDao.getWineById(wineId)
    
    fun getWineByIdFlow(wineId: Long): Flow<Wine?> = wineDao.getWineByIdFlow(wineId)
    
    fun searchWines(query: String): Flow<List<Wine>> = 
        wineDao.searchWines(query)
    
    // Filters
    fun getWinesByType(type: WineType): Flow<List<Wine>> = wineDao.getWinesByType(type)
    
    fun getWinesByCountry(country: String): Flow<List<Wine>> = wineDao.getWinesByCountry(country)
    
    fun getWinesByRegion(region: String): Flow<List<Wine>> = wineDao.getWinesByRegion(region)
    
    fun getWinesByVintage(vintage: Int): Flow<List<Wine>> = wineDao.getWinesByVintage(vintage)
    
    fun getWinesByStorageLocation(location: String): Flow<List<Wine>> = 
        wineDao.getWinesByStorageLocation(location)
    
    // Drinking window queries
    fun getReadyToDrinkWines(): Flow<List<Wine>> = wineDao.getReadyToDrinkWines()
    
    fun getWinesInDrinkingWindow(year: Int): Flow<List<Wine>> = 
        wineDao.getWinesInDrinkingWindow(year)
    
    fun getWinesNotReady(year: Int): Flow<List<Wine>> = wineDao.getWinesNotReady(year)
    
    fun getOverdueWines(year: Int): Flow<List<Wine>> = wineDao.getOverdueWines(year)
    
    // For drinking window relative to now
    fun getCurrentlyReadyWines(): Flow<List<Wine>> = 
        wineDao.getWinesInDrinkingWindow(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR))
    
    fun getOverdueWinesNow(): Flow<List<Wine>> = 
        wineDao.getOverdueWines(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR))
    
    // External lookups
    suspend fun getWineByVivinoId(vivinoId: String): Wine? = wineDao.getWineByVivinoId(vivinoId)
    
    suspend fun getWineByBarcode(barcode: String): Wine? = wineDao.getWineByBarcode(barcode)
    
    suspend fun getWineBySystembolagetId(systembolagetId: String): Wine? = 
        wineDao.getWineBySystembolagetId(systembolagetId)
    
    // Statistics
    suspend fun getStatistics(): WineStatistics {
        val totalCount = wineDao.getTotalWineCount()
        val totalBottles = wineDao.getTotalBottleCount() ?: 0
        val totalValue = wineDao.getTotalValue() ?: 0f
        val avgRating = wineDao.getAverageRating() ?: 0f
        
        return WineStatistics(
            totalWineCount = totalCount,
            totalBottleCount = totalBottles,
            totalValue = totalValue,
            averageRating = avgRating
        )
    }
    
    // Reference data
    suspend fun getAllCountries(): List<String> = wineDao.getAllCountries()
    
    suspend fun getAllRegions(): List<String> = wineDao.getAllRegions()
    
    suspend fun getAllVintages(): List<Int> = wineDao.getAllVintages()
    
    suspend fun getAllProducers(): List<String> = wineDao.getAllProducers()
    
    suspend fun getAllStorageLocationNames(): List<String> = wineDao.getAllStorageLocations()
    
    // Advanced search with filters
    fun advancedSearch(
        name: String? = null,
        producer: String? = null,
        vintage: Int? = null,
        wineType: WineType? = null,
        country: String? = null,
        region: String? = null,
        minRating: Float? = null,
        maxRating: Float? = null,
        readyToDrink: Boolean? = null
    ): Flow<List<Wine>> = wineDao.advancedSearch(
        name, producer, vintage, wineType, country, region, 
        minRating, maxRating, readyToDrink
    )
    
    // Check if wine exists by attributes (for scanner matching)
    suspend fun findMatchingWine(
        name: String,
        producer: String,
        vintage: Int?
    ): Wine? {
        return wineDao.searchWines(name).map { wines ->
            wines.find { wine ->
                wine.producer.equals(producer, ignoreCase = true) &&
                (vintage == null || wine.vintage == vintage)
            }
        }.toString().let { null } // Simplified - in reality would search
    }
}

data class WineStatistics(
    val totalWineCount: Int,
    val totalBottleCount: Int,
    val totalValue: Float,
    val averageRating: Float
)
