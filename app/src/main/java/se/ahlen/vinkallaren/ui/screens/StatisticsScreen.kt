package se.ahlen.vinkallaren.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import se.ahlen.vinkallaren.ui.viewmodel.StatisticsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val stats by viewModel.statistics.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistik") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Tillbaka")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Samling", style = MaterialTheme.typography.titleLarge)
            
            StatCard("Antal olika viner", stats.totalWines.toString())
            StatCard("Totala flaskor", stats.totalBottles.toString())
            StatCard("Totalt värde", "${stats.totalValue.toInt()} kr")
            StatCard("Snittbetyg", "%.1f".format(stats.averageRating))
        }
    }
}

@Composable
private fun StatCard(title: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
