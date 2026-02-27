package se.ahlen.vinkallaren.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import se.ahlen.vinkallaren.data.model.WineType
import se.ahlen.vinkallaren.ui.viewmodel.AddWineViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddWineScreen(
    navController: NavController,
    viewModel: AddWineViewModel = hiltViewModel(),
    scannedName: String? = null,
    scannedProducer: String? = null,
    scannedVintage: Int? = null,
    scannedType: WineType? = null
) {
    var name by remember { mutableStateOf(scannedName ?: "") }
    var producer by remember { mutableStateOf(scannedProducer ?: "") }
    var vintage by remember { mutableStateOf(scannedVintage?.toString() ?: "") }
    var wineType by remember { mutableStateOf(scannedType ?: WineType.RED) }
    var country by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    var storageLocation by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lägg till vin") },
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
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Vinnamn *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Producer
                OutlinedTextField(
                    value = producer,
                    onValueChange = { producer = it },
                    label = { Text("Producent *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Wine type dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = wineType.displayName(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Typ") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        WineType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName()) },
                                onClick = {
                                    wineType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Vintage
                    OutlinedTextField(
                        value = vintage,
                        onValueChange = { if (it.length <= 4) vintage = it },
                        label = { Text("Årgång") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    // Quantity
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Antal") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                
                // Country
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Land") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Region
                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Region") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Price
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Pris (SEK)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                
                // Storage location
                OutlinedTextField(
                    value = storageLocation,
                    onValueChange = { storageLocation = it },
                    label = { Text("Förvaringsplats") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Action buttons
                Row {
                    OutlinedButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Avbryt")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        enabled = name.isNotBlank() && producer.isNotBlank(),
                        onClick = {
                            viewModel.addWine(
                                name = name,
                                producer = producer,
                                wineType = wineType,
                                vintage = vintage.toIntOrNull(),
                                country = country.takeIf { it.isNotBlank() },
                                region = region.takeIf { it.isNotBlank() },
                                quantity = quantity.toIntOrNull() ?: 1,
                                price = price.toFloatOrNull(),
                                storageLocation = storageLocation.takeIf { it.isNotBlank() }
                            )
                            navController.navigateUp()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Spara")
                    }
                }
            }
        }
    }
}