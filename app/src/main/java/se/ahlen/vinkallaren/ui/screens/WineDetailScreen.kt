package se.ahlen.vinkallaren.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import se.ahlen.vinkallaren.ui.viewmodel.WineDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineDetailScreen(
    navController: NavController,
    wineId: Long,
    viewModel: WineDetailViewModel = hiltViewModel()
) {
    val wine by viewModel.getWine(wineId).collectAsState(initial = null)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vin") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Tillbaka")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Edit */ }) {
                        Icon(Icons.Default.Edit, "Redigera")
                    }
                }
            )
        }
    ) { padding ->
        wine?.let { w ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                Text(w.name, style = MaterialTheme.typography.headlineMedium)
                Text(w.producer, style = MaterialTheme.typography.titleMedium)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                w.vintage?.let { Text("Årgång: $it") }
                Text("Typ: ${w.wineType.displayName()}")
                w.country?.let { Text("Land: $it") }
                w.region?.let { Text("Region: $it") }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Förvaras: ${w.quantity} flaskor")
                w.storageLocation?.let { Text("Plats: $it") }
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
