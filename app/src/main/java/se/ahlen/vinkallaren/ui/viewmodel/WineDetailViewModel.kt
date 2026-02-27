package se.ahlen.vinkallaren.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import se.ahlen.vinkallaren.data.model.Wine
import se.ahlen.vinkallaren.data.repository.WineRepository
import javax.inject.Inject

@HiltViewModel
class WineDetailViewModel @Inject constructor(
    private val wineRepository: WineRepository
) : ViewModel() {
    
    fun getWine(wineId: Long): Flow<Wine?> {
        return wineRepository.getWineByIdFlow(wineId)
    }
    
    suspend fun deleteWine(wine: Wine) {
        wineRepository.deleteWine(wine)
    }
}
