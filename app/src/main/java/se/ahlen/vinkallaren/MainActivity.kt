package se.ahlen.vinkallaren

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import se.ahlen.vinkallaren.ui.screens.AddWineScreen
import se.ahlen.vinkallaren.ui.screens.HomeScreen
import se.ahlen.vinkallaren.ui.screens.ScannerScreen
import se.ahlen.vinkallaren.ui.screens.SearchScreen
import se.ahlen.vinkallaren.ui.screens.SettingsScreen
import se.ahlen.vinkallaren.ui.screens.StatisticsScreen
import se.ahlen.vinkallaren.ui.screens.WineDetailScreen
import se.ahlen.vinkallaren.ui.screens.WineListScreen
import se.ahlen.vinkallaren.ui.screens.ReadyToDrinkScreen
import se.ahlen.vinkallaren.ui.theme.VinkallarenTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VinkallarenTheme {
                VinkallarenApp()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector?) {
    object Home : Screen("home", "Hem", Icons.Default.Home)
    object WineList : Screen("wineList", "Viner", Icons.Default.WineBar)
    object Statistics : Screen("statistics", "Statistik", null)
    object ReadyToDrink : Screen("readyToDrink", "Redo att dricka", null)
    object Scanner : Screen("scanner", "Skanner", Icons.Default.Search)
    object Search : Screen("search", "Sök", null)
    object Settings : Screen("settings", "Inställningar", Icons.Default.Settings)
    object WineDetail : Screen("wineDetail/{wineId}", "Vin", null)
    object AddWine : Screen("addWine", "Lägg till vin", Icons.Default.Add)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VinkallarenApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val bottomNavItems = listOf(Screen.Home, Screen.WineList, Screen.Scanner, Screen.Settings)
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vinkällaren") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentDestination?.route == Screen.WineList.route) {
                FloatingActionButton(onClick = { navController.navigate(Screen.AddWine.route) }) {
                    Icon(Icons.Default.Add, contentDescription = "Lägg till vin")
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.WineList.route) { WineListScreen(navController) }
            composable(Screen.Statistics.route) { StatisticsScreen(navController) }
            composable(Screen.ReadyToDrink.route) { ReadyToDrinkScreen(navController) }
            composable(Screen.Scanner.route) { ScannerScreen(navController) }
            composable(Screen.Search.route) { SearchScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
            composable(Screen.WineDetail.route) { backStackEntry ->
                val wineId = backStackEntry.arguments?.getString("wineId")?.toLongOrNull() ?: 0L
                WineDetailScreen(navController, wineId)
            }
            composable(Screen.AddWine.route) { AddWineScreen(navController) }
        }
    }
}
