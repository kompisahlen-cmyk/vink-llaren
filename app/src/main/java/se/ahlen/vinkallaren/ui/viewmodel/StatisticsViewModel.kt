package se.ahlen.vinkallaren.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import se.ahlen.vinkallaren.data.repository.WineRepository
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val wineRepository: WineRepository
) : ViewModel() {
    
    val statistics: StateFlow<StatsData> = flow {
        val stats = wineRepository.getStatistics()
        emit(StatsData(
            totalWines = stats.totalWineCount,
            totalBottles = stats.totalBottleCount,
            totalValue = stats.totalValue,
            averageRating = stats.averageRating
        ))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsData()
    )
}

data class StatsData(
    val totalWines: Int = 0,
    val totalBottles: Int = 0,
    val totalValue: Float = 0f,
    val averageRating: Float = 0f
)
