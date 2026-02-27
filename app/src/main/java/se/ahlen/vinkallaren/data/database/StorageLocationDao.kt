package se.ahlen.vinkallaren.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import se.ahlen.vinkallaren.data.model.StorageLocation

@Dao
interface StorageLocationDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: StorageLocation): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<StorageLocation>): List<Long>
    
    @Update
    suspend fun updateLocation(location: StorageLocation)
    
    @Delete
    suspend fun deleteLocation(location: StorageLocation)
    
    @Query("SELECT * FROM storage_locations WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveLocations(): Flow<List<StorageLocation>>
    
    @Query("SELECT * FROM storage_locations ORDER BY name ASC")
    fun getAllLocations(): Flow<List<StorageLocation>>
    
    @Query("SELECT * FROM storage_locations WHERE id = :locationId")
    suspend fun getLocationById(locationId: Long): StorageLocation?
    
    @Query("SELECT * FROM storage_locations WHERE name = :name LIMIT 1")
    suspend fun getLocationByName(name: String): StorageLocation?
    
    @Query("SELECT COUNT(*) FROM storage_locations WHERE isActive = 1")
    suspend fun getActiveLocationCount(): Int
    
    @Query("UPDATE storage_locations SET currentCount = currentCount + :amount WHERE id = :locationId")
    suspend fun incrementBottleCount(locationId: Long, amount: Int = 1)
    
    @Query("UPDATE storage_locations SET currentCount = currentCount - :amount WHERE id = :locationId AND currentCount >= :amount")
    suspend fun decrementBottleCount(locationId: Long, amount: Int = 1): Int
}
