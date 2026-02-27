package se.ahlen.vinkallaren.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import se.ahlen.vinkallaren.data.model.TastingNote
import se.ahlen.vinkallaren.data.model.Wine
import se.ahlen.vinkallaren.data.model.StorageLocation

@Database(
    entities = [
        Wine::class,
        TastingNote::class,
        StorageLocation::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WineDatabase : RoomDatabase() {
    
    abstract fun wineDao(): WineDao
    abstract fun tastingNoteDao(): TastingNoteDao
    abstract fun storageLocationDao(): StorageLocationDao
    
    companion object {
        @Volatile
        private var INSTANCE: WineDatabase? = null
        
        fun getDatabase(context: Context): WineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WineDatabase::class.java,
                    "vinkallaren_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
