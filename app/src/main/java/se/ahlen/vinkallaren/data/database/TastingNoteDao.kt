package se.ahlen.vinkallaren.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import se.ahlen.vinkallaren.data.model.TastingNote

@Dao
interface TastingNoteDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTastingNote(note: TastingNote): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTastingNotes(notes: List<TastingNote>): List<Long>
    
    @Update
    suspend fun updateTastingNote(note: TastingNote)
    
    @Delete
    suspend fun deleteTastingNote(note: TastingNote)
    
    @Query("DELETE FROM tasting_notes WHERE id = :noteId")
    suspend fun deleteTastingNoteById(noteId: Long)
    
    @Query("SELECT * FROM tasting_notes WHERE wineId = :wineId ORDER BY tastingDate DESC")
    fun getTastingNotesForWine(wineId: Long): Flow<List<TastingNote>>
    
    @Query("SELECT * FROM tasting_notes WHERE wineId = :wineId ORDER BY tastingDate DESC")
    fun getTastingNotesForWineLive(wineId: Long): LiveData<List<TastingNote>>
    
    @Query("SELECT * FROM tasting_notes WHERE id = :noteId")
    suspend fun getTastingNoteById(noteId: Long): TastingNote?
    
    @Query("SELECT * FROM tasting_notes ORDER BY tastingDate DESC")
    fun getAllTastingNotes(): Flow<List<TastingNote>>
    
    @Query("SELECT * FROM tasting_notes ORDER BY tastingDate DESC LIMIT :limit")
    fun getRecentTastingNotes(limit: Int): Flow<List<TastingNote>>
    
    @Query("SELECT AVG(score) FROM tasting_notes WHERE wineId = :wineId AND score IS NOT NULL")
    suspend fun getAverageScoreForWine(wineId: Long): Float?
    
    @Query("SELECT COUNT(*) FROM tasting_notes WHERE wineId = :wineId")
    suspend fun getTastingNoteCountForWine(wineId: Long): Int
    
    @Query("SELECT * FROM tasting_notes WHERE isSynced = 0")
    suspend fun getUnsyncedNotes(): List<TastingNote>
    
    @Query("UPDATE tasting_notes SET isSynced = 1 WHERE id = :noteId")
    suspend fun markAsSynced(noteId: Long)
}
