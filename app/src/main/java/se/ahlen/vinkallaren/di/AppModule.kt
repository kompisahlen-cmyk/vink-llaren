package se.ahlen.vinkallaren.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import se.ahlen.vinkallaren.data.database.WineDao
import se.ahlen.vinkallaren.data.database.WineDatabase
import se.ahlen.vinkallaren.data.database.TastingNoteDao
import se.ahlen.vinkallaren.data.database.StorageLocationDao
import se.ahlen.vinkallaren.data.remote.RoboflowDataSource
import se.ahlen.vinkallaren.data.repository.WineRepository
import se.ahlen.vinkallaren.data.repository.RoboflowRepository
import se.ahlen.vinkallaren.scanner.WineScanner
import se.ahlen.vinkallaren.utils.ImageCropper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WineDatabase {
        return Room.databaseBuilder(
            context,
            WineDatabase::class.java,
            "vinkallaren_database"
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideWineDao(database: WineDatabase): WineDao = database.wineDao()
    
    @Provides
    @Singleton
    fun provideTastingNoteDao(database: WineDatabase): TastingNoteDao = database.tastingNoteDao()
    
    @Provides
    @Singleton
    fun provideStorageLocationDao(database: WineDatabase): StorageLocationDao = database.storageLocationDao()
    
    @Provides
    @Singleton
    fun provideWineRepository(wineDao: WineDao): WineRepository = WineRepository(wineDao)
    
    @Provides
    @Singleton
    fun provideWineScanner(@ApplicationContext context: Context): WineScanner = WineScanner(context)
    
    // Roboflow & Image Processing Dependencies
    @Provides
    @Singleton
    fun provideImageCropper(@ApplicationContext context: Context): ImageCropper = ImageCropper(context)
    
    @Provides
    @Singleton
    fun provideRoboflowDataSource(@ApplicationContext context: Context): RoboflowDataSource = 
        RoboflowDataSource(context)
    
    @Provides
    @Singleton
    fun provideRoboflowRepository(
        dataSource: RoboflowDataSource,
        imageCropper: ImageCropper
    ): RoboflowRepository = RoboflowRepository(dataSource, imageCropper)
}
