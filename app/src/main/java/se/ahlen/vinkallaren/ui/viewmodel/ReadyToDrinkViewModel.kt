package se.ahlen.vinkallaren.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import se.ahlen.vinkallaren.data.model.Wine
import se.ahlen.vinkallaren.data.repository.WineRepository
import javax.inject.Inject

@HiltViewModel
class ReadyToDrinkViewModel @Inject constructor(
    private val wineRepository: WineRepository
) : ViewModel() {
    
    val readyWines: StateFlow<List<Wine>> = wineRepository.getReadyToDrinkWines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
