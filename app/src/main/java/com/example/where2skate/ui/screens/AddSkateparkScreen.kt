package com.example.where2skate.ui.screens

// Importaciones necesarias, incluyendo los iconos estándar de Material
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt // Icono estándar
import androidx.compose.material.icons.filled.Description    // Icono estándar
import androidx.compose.material.icons.filled.EditLocation     // Icono estándar
import androidx.compose.material.icons.filled.Skateboarding  // Icono estándar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.where2skate.ui.viewmodel.SkateparkViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSkateparkScreen(
    skateparkViewModel: SkateparkViewModel = viewModel(),
    initialLatLng: LatLng?,
    onSkateparkAdded: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    val locationText = initialLatLng?.let { "Lat: ${"%.4f".format(it.latitude)}, Lng: ${"%.4f".format(it.longitude)}" } ?: "No location selected"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Usa el color de fondo del tema
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AddLocationAlt, // Icono estándar
                    contentDescription = "Add Skatepark Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "New Spot",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Location Pinpointed:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        locationText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            StyledOutlinedTextField( // Usando nuestro Composable personalizado
                value = name,
                onValueChange = { name = it },
                label = "Spot Name*",
                modifier = Modifier.fillMaxWidth(), // Icono estándar
                leadingIcon = Icons.Filled.Skateboarding,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(12.dp))

            StyledOutlinedTextField( // Usando nuestro Composable personalizado
                value = description,
                onValueChange = { description = it },
                label = "Description / Vibe",
                modifier = Modifier.fillMaxWidth(), // Icono estándar
                leadingIcon = Icons.Filled.Description,
                minLines = 3,
                maxLines = 5,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(12.dp))

            StyledOutlinedTextField( // Usando nuestro Composable personalizado
                value = address,
                onValueChange = { address = it },
                label = "Address (Optional)",
                modifier = Modifier.fillMaxWidth(), // Icono estándar
                leadingIcon = Icons.Filled.EditLocation,
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && initialLatLng != null) {
                        skateparkViewModel.addSkatepark(
                            name = name,
                            description = description.ifBlank { null },
                            location = GeoPoint(initialLatLng.latitude, initialLatLng.longitude),
                            address = address.ifBlank { null }
                        )
                        onSkateparkAdded()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                enabled = name.isNotBlank() && initialLatLng != null,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
            ) {
                Text(
                    "DROP PIN!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Composable reutilizable para los TextFields con estilo (necesita estar en el mismo archivo o importado)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyledOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null, // Acepta ImageVector
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    visualTransformation: VisualTransformation
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        leadingIcon = leadingIcon?.let {
            { Icon(imageVector = it, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
        )
    )
}