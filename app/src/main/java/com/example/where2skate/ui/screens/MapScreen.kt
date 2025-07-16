package com.example.where2skate.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
// import androidx.compose.foundation.background // No se usa directamente aquí, MaterialTheme.colorScheme.background lo maneja
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
// import androidx.compose.ui.draw.clip // No se usa directamente aquí, el shape del FAB lo maneja
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.where2skate.ui.viewmodel.AuthViewModel
import com.example.where2skate.ui.viewmodel.SkateparkViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// --- IMPORTACIONES CLAVE QUE PODRÍAN FALTAR O ESTAR INCORRECTAS ---
import androidx.compose.foundation.layout.Row  // <<<--- ¡ASEGÚRATE DE QUE ESTA LÍNEA ESTÉ!
import androidx.compose.foundation.layout.Spacer // <<<--- ¡ASEGÚRATE DE QUE ESTA LÍNEA ESTÉ!
import androidx.compose.foundation.layout.width  // <<<--- ¡ASEGÚRATE DE QUE ESTA LÍNEA ESTÉ! (para Modifier.width)
import androidx.compose.ui.Alignment           // <<<--- ¡ASEGÚRATE DE QUE ESTA LÍNEA ESTÉ! (para Alignment.CenterVertically)
import androidx.compose.ui.Modifier             // <<<--- AUNQUE ES COMÚN, VERIFICA ESTA TAMBIÉN

// --- FIN DE IMPORTACIONES CLAVE ---

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    skateparkViewModel: SkateparkViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onNavigateToAddSkatepark: (LatLng) -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val skateparks by skateparkViewModel.skateparks.collectAsState()

    val initialZoomLocation = LatLng(19.0204, -98.2469) // Tlaxcalancingo, Puebla
    val initialZoomLevel = 12f

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialZoomLocation, initialZoomLevel)
    }
    var showDialog by remember { mutableStateOf(false) }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    val locationPermissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    val locationPermissionsState = rememberMultiplePermissionsState(locationPermissions)
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var mapProperties by remember {
        mutableStateOf(MapProperties(isMyLocationEnabled = locationPermissionsState.allPermissionsGranted))
    }
    var mapUiSettings by remember {
        mutableStateOf(MapUiSettings(
            myLocationButtonEnabled = locationPermissionsState.allPermissionsGranted,
            zoomControlsEnabled = true,
            mapToolbarEnabled = false
        ))
    }

    LaunchedEffect(locationPermissionsState.allPermissionsGranted) {
        if (locationPermissionsState.allPermissionsGranted) {
            Log.d("MapScreen", "Permisos de ubicación concedidos.")
            mapProperties = mapProperties.copy(isMyLocationEnabled = true)
            mapUiSettings = mapUiSettings.copy(myLocationButtonEnabled = true)
            scope.launch {
                try {
                    val location = fusedLocationClient.lastLocation.await()
                    location?.let {
                        val currentLatLng = LatLng(it.latitude, it.longitude)
                        Log.d("MapScreen", "Ubicación obtenida: $currentLatLng")
                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    } ?: run {
                        Log.d("MapScreen", "No se pudo obtener la última ubicación conocida (es nula).")
                    }
                } catch (e: SecurityException) {
                    Log.e("MapScreen", "Excepción de seguridad al obtener la ubicación", e)
                    mapProperties = mapProperties.copy(isMyLocationEnabled = false)
                    mapUiSettings = mapUiSettings.copy(myLocationButtonEnabled = false)
                } catch (e: Exception) {
                    Log.e("MapScreen", "Error desconocido al obtener la ubicación", e)
                }
            }
        } else {
            Log.d("MapScreen", "Permisos de ubicación no concedidos o parcialmente concedidos.")
            mapProperties = mapProperties.copy(isMyLocationEnabled = false)
            mapUiSettings = mapUiSettings.copy(myLocationButtonEnabled = false)
            if (locationPermissionsState.shouldShowRationale) {
                Log.d("MapScreen", "Debería mostrarse justificación para permisos.")
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!locationPermissionsState.allPermissionsGranted) {
            Log.d("MapScreen", "Solicitando permisos de ubicación iniciales...")
            locationPermissionsState.launchMultiplePermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) { // Ahora debería funcionar
                        Icon(
                            imageVector = Icons.Filled.Map,
                            contentDescription = "Map Icon",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // Ahora debería funcionar
                        Text("Where2Skate", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        onLogout()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val currentMapCenter = cameraPositionState.position.target
                    onNavigateToAddSkatepark(currentMapCenter)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Add, "Add Skatepark")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = mapUiSettings,
                properties = mapProperties,
                onMapLongClick = { latLng ->
                    selectedLatLng = latLng
                    showDialog = true
                }
            ) {
                skateparks.forEach { park ->
                    park.location?.let { geoPoint ->
                        Marker(
                            state = MarkerState(position = LatLng(geoPoint.latitude, geoPoint.longitude)),
                            title = park.name,
                            snippet = park.description
                        )
                    }
                }
            }

            if (showDialog && selectedLatLng != null) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        selectedLatLng = null
                    },
                    title = { Text("Add Skatepark Here?", fontWeight = FontWeight.SemiBold) },
                    text = { Text("Do you want to add a new skatepark at this location?\nLat: ${"%.4f".format(selectedLatLng!!.latitude)}, Lng: ${"%.4f".format(selectedLatLng!!.longitude)}") },
                    confirmButton = {
                        Button(
                            onClick = {
                                onNavigateToAddSkatepark(selectedLatLng!!)
                                showDialog = false
                                selectedLatLng = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("YES, ADD SPOT", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                selectedLatLng = null
                            }
                        ) {
                            Text("CANCEL", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}