package se.ahlen.vinkallaren.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import se.ahlen.vinkallaren.ui.viewmodel.ReadyToDrinkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadyToDrinkScreen(
    navController: NavController,
    viewModel: ReadyToDrinkViewModel = hiltViewModel()
) {
    val wines by viewModel.readyWines.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Redo att dricka") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Tillbaka")
                    }
                }
            )
        }
    ) { padding ->
        if (wines.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.LocalBar,
                        null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Inga viner redo än", style = MaterialTheme.typography.titleMedium)
                    Text("Vänta på att dina viner mognar", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(wines) { wine ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { navController.navigate("wineDetail/${wine.id}") }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(wine.name, style = MaterialTheme.typography.titleMedium)
                            Text(wine.producer, style = MaterialTheme.typography.bodyMedium)
                            wine.peakMaturityYear?.let {
                                Text("Toppår: $it", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
