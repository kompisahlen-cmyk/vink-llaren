package se.ahlen.vinkallaren.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import se.ahlen.vinkallaren.MainActivity.Screen
import se.ahlen.vinkallaren.data.model.Wine
import se.ahlen.vinkallaren.ui.viewmodel.WineListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WineListScreen(
    navController: NavController,
    viewModel: WineListViewModel = hiltViewModel()
) {
    val wines by viewModel.wines.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dina viner") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                        Icon(Icons.Default.Search, contentDescription = "Sök")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Sök efter namn eller producent...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true
            )
            
            if (wines.isEmpty()) {
                EmptyWineList()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(wines) { wine ->
                        WineCard(
                            wine = wine,
                            onClick = { navController.navigate("wineDetail/${wine.id}") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WineCard(
    wine: Wine,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = wine.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = wine.producer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (wine.quantity > 1) {
                    Badge { Text("${wine.quantity}") }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                wine.vintage?.let {
                    AssistChip(onClick = {}, label = { Text(it.toString()) })
                }
                AssistChip(
                    onClick = {},
                    label = { Text(wine.wineType.displayName()) }
                )
                wine.personalRating?.let { rating ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("%.1f".format(rating))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyWineList() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.WineBar,
                null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Inga viner än",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "Börja med att skanna eller lägga till ett vin",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
