package se.ahlen.vinkallaren.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import se.ahlen.vinkallaren.data.model.Wine
import se.ahlen.vinkallaren.data.model.WineType
import se.ahlen.vinkallaren.data.repository.WineRepository
import se.ahlen.vinkallaren.analysis.WineAnalyzer
import javax.inject.Inject

@HiltViewModel
class AddWineViewModel @Inject constructor(
    private val wineRepository: WineRepository
) : ViewModel() {
    
    fun addWine(
        name: String,
        producer: String,
        wineType: WineType,
        vintage: Int?,
        country: String?,
        region: String?,
        quantity: Int,
        price: Float?,
        storageLocation: String?
    ) {
        viewModelScope.launch {
            // Calculate drinking window
            val window = vintage?.let { 
                WineAnalyzer.calculateDrinkingWindow(wineType, it, region, null)
            }
            
            val wine = Wine(
                name = name,
                producer = producer,
                vintage = vintage,
                wineType = wineType,
                country = country,
                region = region,
                quantity = quantity,
                purchasePrice = price,
                storageLocation = storageLocation,
                drinkingWindowStart = window?.start,
                drinkingWindowEnd = window?.end,
                peakMaturityYear = window?.peak,
                isReadyToDrink = window?.let { 
                    WineAnalyzer.getCurrentYear() >= it.start 
                } ?: false
            )
            
            wineRepository.insertWine(wine)
        }
    }
}
