package se.ahlen.vinkallaren.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Inställningar") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            ListItem(
                headlineContent = { Text("Molnsynkronisering") },
                supportingContent = { Text("Synka med Firebase") },
                leadingContent = { Icon(Icons.Default.Cloud, null) }
            )
            ListItem(
                headlineContent = { Text("Notifikationer") },
                supportingContent = { Text("Påminnelser om viner redo att dricka") },
                leadingContent = { Icon(Icons.Default.Notifications, null) }
            )
            ListItem(
                headlineContent = { Text("Databas") },
                supportingContent = { Text("Exportera/import") },
                leadingContent = { Icon(Icons.Default.Storage, null) }
            )
            ListItem(
                headlineContent = { Text("Om Vinkällaren") },
                supportingContent = { Text("Version 1.0.0") },
                leadingContent = { Icon(Icons.Default.Info, null) }
            )
        }
    }
}
