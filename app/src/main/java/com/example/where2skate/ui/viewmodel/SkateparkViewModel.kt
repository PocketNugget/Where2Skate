package com.example.where2skate.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.where2skate.data.model.Skatepark
import com.example.where2skate.data.repository.SkateparkRepository // Importa el repositorio
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SkateparkViewModel(
    private val skateparkRepository: SkateparkRepository = SkateparkRepository() // Instancia el repo
) : ViewModel() {

    private val _skateparks = MutableStateFlow<List<Skatepark>>(emptyList())
    val skateparks: StateFlow<List<Skatepark>> = _skateparks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchSkateparks()
    }

    fun fetchSkateparks() {
        viewModelScope.launch {
            _isLoading.value = true
            skateparkRepository.getAllSkateparks()
                .catch { e ->
                    Log.e("SkateparkViewModel", "Error fetching skateparks flow", e)
                    _error.value = "Error fetching skateparks: ${e.message}"
                    _isLoading.value = false
                }
                .collect { parks ->
                    _skateparks.value = parks
                    _isLoading.value = false
                    _error.value = null // Limpiar error si fue exitoso
                    Log.d("SkateparkViewModel", "Fetched ${parks.size} parks")
                }
        }
    }

    fun addSkatepark(name: String, description: String?, location: GeoPoint, address: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = skateparkRepository.addSkatepark(name, description, location, address)
            result.fold(
                onSuccess = { parkId ->
                    Log.i("SkateparkViewModel", "Skatepark added successfully: $parkId")
                    // El Flow de getAllSkateparks debería actualizar la lista automáticamente si está activo
                    // Si no, podrías llamar a fetchSkateparks() o manejar la actualización de UI de otra forma.
                },
                onFailure = { e ->
                    Log.e("SkateparkViewModel", "Error adding skatepark", e)
                    _error.value = "Failed to add skatepark: ${e.message}"
                }
            )
            _isLoading.value = false
        }
    }

    fun addRatingToSkatepark(skateparkId: String, ratingValue: Float, comment: String?) {
        viewModelScope.launch {
            // Similar al addSkatepark, manejar _isLoading y _error
            val result = skateparkRepository.addRating(skateparkId, ratingValue, comment)
            result.fold(
                onSuccess = {
                    Log.i("SkateparkViewModel", "Rating added for $skateparkId")
                    // Aquí deberías actualizar la info del skatepark específico o re-obtenerlo
                    // para reflejar el nuevo rating (si el promedio se calcula en el cliente o
                    // si el listener del skatepark individual se actualiza).
                },
                onFailure = { e ->
                    _error.value = "Failed to add rating: ${e.message}"
                }
            )
        }
    }

    // ... otras funciones del ViewModel que usen el repositorio
}