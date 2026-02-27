package se.ahlen.vinkallaren.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import se.ahlen.vinkallaren.data.repository.WineRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wineRepository: WineRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadStatistics()
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                val stats = wineRepository.getStatistics()
                val readyToDrink = wineRepository.getReadyToDrinkWines()
                
                readyToDrink.collect { wines ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            totalWines = stats.totalWineCount,
                            totalBottles = stats.totalBottleCount,
                            totalValue = stats.totalValue,
                            averageRating = stats.averageRating,
                            readyToDrinkCount = wines.size,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}

data class HomeUiState(
    val totalWines: Int = 0,
    val totalBottles: Int = 0,
    val totalValue: Float = 0f,
    val averageRating: Float = 0f,
    val readyToDrinkCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)
