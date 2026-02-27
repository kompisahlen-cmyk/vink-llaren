package se.ahlen.vinkallaren.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import se.ahlen.vinkallaren.data.model.Wine
import se.ahlen.vinkallaren.data.repository.WineRepository
import javax.inject.Inject

@HiltViewModel
class WineListViewModel @Inject constructor(
    private val wineRepository: WineRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _filter = MutableStateFlow<FilterType?>(null)
    
    val wines: StateFlow<List<Wine>> = combine(
        _searchQuery,
        _filter
    ) { query, filter ->
        Pair(query, filter)
    }.flatMapLatest { (query, filter) ->
        when {
            query.isNotBlank() -> wineRepository.searchWines(query)
            filter != null -> when (filter) {
                FilterType.READY_TO_DRINK -> wineRepository.getReadyToDrinkWines()
                else -> wineRepository.getAllWines()
            }
            else -> wineRepository.getAllWines()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun setFilter(type: FilterType?) {
        _filter.value = type
    }
}

enum class FilterType {
    RED, WHITE, ROSE, SPARKLING, READY_TO_DRINK
}
