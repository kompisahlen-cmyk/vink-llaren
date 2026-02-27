package se.ahlen.vinkallaren.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import se.ahlen.vinkallaren.data.model.Wine
import se.ahlen.vinkallaren.data.model.WineType

@Dao
interface WineDao {
    
    // Insert operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWine(wine: Wine): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWines(wines: List<Wine>): List<Long>
    
    // Update operations
    @Update
    suspend fun updateWine(wine: Wine)
    
    @Query("UPDATE wines SET quantity = quantity + :amount WHERE id = :wineId")
    suspend fun incrementQuantity(wineId: Long, amount: Int = 1)
    
    @Query("UPDATE wines SET quantity = quantity - :amount WHERE id = :wineId AND quantity >= :amount")
    suspend fun decrementQuantity(wineId: Long, amount: Int = 1): Int
    
    @Query("UPDATE wines SET quantity = :quantity WHERE id = :wineId")
    suspend fun updateQuantity(wineId: Long, quantity: Int)
    
    // Delete operations
    @Delete
    suspend fun deleteWine(wine: Wine)
    
    @Query("DELETE FROM wines WHERE id = :wineId")
    suspend fun deleteWineById(wineId: Long)
    
    @Query("UPDATE wines SET isDeleted = 1, updatedAt = :updatedAt WHERE id = :wineId")
    suspend fun softDeleteWine(wineId: Long, updatedAt: Long = System.currentTimeMillis())
    
    // Query operations
    @Query("SELECT * FROM wines WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllWines(): Flow<List<Wine>>
    
    @Query("SELECT * FROM wines WHERE isDeleted = 0 ORDER BY createdAt DESC")
    fun getAllWinesLive(): LiveData<List<Wine>>
    
    @Query("SELECT * FROM wines WHERE id = :wineId")
    suspend fun getWineById(wineId: Long): Wine?
    
    @Query("SELECT * FROM wines WHERE id = :wineId")
    fun getWineByIdFlow(wineId: Long): Flow<Wine?>
    
    // Search
    @Query("""
        SELECT * FROM wines 
        WHERE isDeleted = 0 
        AND (name LIKE '%' || :query || '%' 
            OR producer LIKE '%' || :query || '%'
            OR region LIKE '%' || :query || '%'
            OR country LIKE '%' || :query || '%')
        ORDER BY name ASC
    """)
    fun searchWines(query: String): Flow<List<Wine>>
    
    // Filters
    @Query("SELECT * FROM wines WHERE wineType = :type AND isDeleted = 0 ORDER BY name ASC")
    fun getWinesByType(type: WineType): Flow<List<Wine>>
    
    @Query("SELECT * FROM wines WHERE country = :country AND isDeleted = 0 ORDER BY name ASC")
    fun getWinesByCountry(country: String): Flow<List<Wine>>
    
    @Query("SELECT * FROM wines WHERE region = :region AND isDeleted = 0 ORDER BY name ASC")
    fun getWinesByRegion(region: String): Flow<List<Wine>>
    
    @Query("SELECT * FROM wines WHERE vintage = :vintage AND isDeleted = 0 ORDER BY name ASC")
    fun getWinesByVintage(vintage: Int): Flow<List<Wine>>
    
    @Query("SELECT * FROM wines WHERE storageLocation = :location AND isDeleted = 0 ORDER BY name ASC")
    fun getWinesByStorageLocation(location: String): Flow<List<Wine>>
    
    @Query("SELECT * FROM wines WHERE isReadyToDrink = 1 AND isDeleted = 0 ORDER BY peakMaturityYear ASC")
    fun getReadyToDrinkWines(): Flow<List<Wine>>
    
    @Query("""
        SELECT * FROM wines 
        WHERE drinkingWindowStart <= :year 
        AND drinkingWindowEnd >= :year 
        AND isDeleted = 0
        ORDER BY peakMaturityYear ASC
    """)
    fun getWinesInDrinkingWindow(year: Int): Flow<List<Wine>>
    
    @Query("""
        SELECT * FROM wines 
        WHERE drinkingWindowStart > :year 
        AND isDeleted = 0
        ORDER BY drinkingWindowStart ASC
    """)
    fun getWinesNotReady(year: Int): Flow<List<Wine>>
    
    @Query("""
        SELECT * FROM wines 
        WHERE drinkingWindowEnd < :year 
        AND isDeleted = 0
        ORDER BY drinkingWindowEnd DESC
    """)
    fun getOverdueWines(year: Int): Flow<List<Wine>>
    
    // Statistics
    @Query("SELECT COUNT(*) FROM wines WHERE isDeleted = 0")
    suspend fun getTotalWineCount(): Int
    
    @Query("SELECT SUM(quantity) FROM wines WHERE isDeleted = 0")
    suspend fun getTotalBottleCount(): Int?
    
    @Query("SELECT SUM(purchasePrice * quantity) FROM wines WHERE isDeleted = 0 AND purchasePrice IS NOT NULL")
    suspend fun getTotalValue(): Float?
    
    @Query("SELECT AVG(personalRating) FROM wines WHERE isDeleted = 0 AND personalRating IS NOT NULL")
    suspend fun getAverageRating(): Float?
    
    @Query("SELECT DISTINCT country FROM wines WHERE country IS NOT NULL AND isDeleted = 0 ORDER BY country ASC")
    suspend fun getAllCountries(): List<String>
    
    @Query("SELECT DISTINCT region FROM wines WHERE region IS NOT NULL AND isDeleted = 0 ORDER BY region ASC")
    suspend fun getAllRegions(): List<String>
    
    @Query("SELECT DISTINCT vintage FROM wines WHERE vintage IS NOT NULL AND isDeleted = 0 ORDER BY vintage DESC")
    suspend fun getAllVintages(): List<Int>
    
    @Query("SELECT DISTINCT producer FROM wines WHERE producer IS NOT NULL AND isDeleted = 0 ORDER BY producer ASC")
    suspend fun getAllProducers(): List<String>
    
    @Query("SELECT DISTINCT storageLocation FROM wines WHERE storageLocation IS NOT NULL AND isDeleted = 0")
    suspend fun getAllStorageLocations(): List<String>
    
    // External ID lookups
    @Query("SELECT * FROM wines WHERE vivinoId = :vivinoId AND isDeleted = 0 LIMIT 1")
    suspend fun getWineByVivinoId(vivinoId: String): Wine?
    
    @Query("SELECT * FROM wines WHERE barcode = :barcode AND isDeleted = 0 LIMIT 1")
    suspend fun getWineByBarcode(barcode: String): Wine?
    
    @Query("SELECT * FROM wines WHERE systembolagetId = :systembolagetId AND isDeleted = 0 LIMIT 1")
    suspend fun getWineBySystembolagetId(systembolagetId: String): Wine?
    
    // Sync operations
    @Query("SELECT * FROM wines WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedWines(): List<Wine>
    
    @Query("SELECT * FROM wines WHERE isDeleted = 1")
    suspend fun getDeletedWines(): List<Wine>
    
    @Query("UPDATE wines SET isSynced = 1 WHERE id = :wineId")
    suspend fun markAsSynced(wineId: Long)
    
    @Query("UPDATE wines SET firebaseId = :firebaseId WHERE id = :wineId")
    suspend fun updateFirebaseId(wineId: Long, firebaseId: String)
    
    // Advanced search
    @Query("""
        SELECT * FROM wines 
        WHERE isDeleted = 0 
        AND (:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:producer IS NULL OR producer LIKE '%' || :producer || '%')
        AND (:vintage IS NULL OR vintage = :vintage)
        AND (:wineType IS NULL OR wineType = :wineType)
        AND (:country IS NULL OR country = :country)
        AND (:region IS NULL OR region = :region)
        AND (:minRating IS NULL OR personalRating >= :minRating)
        AND (:maxRating IS NULL OR personalRating <= :maxRating)
        AND (:readyToDrink IS NULL OR isReadyToDrink = :readyToDrink)
        ORDER BY createdAt DESC
    """)
    fun advancedSearch(
        name: String?,
        producer: String?,
        vintage: Int?,
        wineType: WineType?,
        country: String?,
        region: String?,
        minRating: Float?,
        maxRating: Float?,
        readyToDrink: Boolean?
    ): Flow<List<Wine>>
}
