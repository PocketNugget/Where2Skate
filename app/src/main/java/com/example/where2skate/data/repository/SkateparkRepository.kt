package com.example.where2skate.data.repository

import android.util.Log
import com.example.where2skate.data.model.Rating
import com.example.where2skate.data.model.Skatepark
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SkateparkRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val skateparksCollection = firestore.collection("skateparks")

    companion object {
        private const val TAG = "SkateparkRepository"
    }

    /**
     * Obtiene un Flow de la lista de skateparks.
     * El Flow emitirá una nueva lista cada vez que haya cambios en Firestore.
     */
    fun getAllSkateparks(): Flow<List<Skatepark>> = callbackFlow {
        val listenerRegistration = skateparksCollection
            .orderBy("createdAt", Query.Direction.DESCENDING) // Opcional: ordenar por fecha de creación
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error)
                    close(error) // Cierra el Flow con error
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val parks = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject<Skatepark>()?.copy(id = doc.id)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error converting document ${doc.id}", e)
                            null
                        }
                    }
                    Log.d(TAG, "Current skateparks: ${parks.size}")
                    trySend(parks).isSuccess // Emite la nueva lista
                }
            }
        // Cuando el Flow es cancelado, removemos el listener
        awaitClose {
            Log.d(TAG, "Cancelling skateparks listener")
            listenerRegistration.remove()
        }
    }

    /**
     * Obtiene un skatepark específico por su ID.
     * Retorna un Flow para escuchar cambios en tiempo real o null si no se encuentra.
     */
    fun getSkateparkById(skateparkId: String): Flow<Skatepark?> = callbackFlow {
        val docRef = skateparksCollection.document(skateparkId)
        val listenerRegistration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed for skatepark $skateparkId", error)
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                try {
                    val park = snapshot.toObject<Skatepark>()?.copy(id = snapshot.id)
                    trySend(park).isSuccess
                } catch (e: Exception) {
                    Log.e(TAG, "Error converting document $skateparkId", e)
                    trySend(null).isSuccess // o close(e)
                }
            } else {
                Log.d(TAG, "No such skatepark: $skateparkId")
                trySend(null).isSuccess
            }
        }
        awaitClose {
            Log.d(TAG, "Cancelling listener for skatepark $skateparkId")
            listenerRegistration.remove()
        }
    }


    /**
     * Añade un nuevo skatepark a Firestore.
     * Retorna el ID del nuevo skatepark o null en caso de error.
     */
    suspend fun addSkatepark(name: String, description: String?, location: GeoPoint, address: String?): Result<String> {
        val user = auth.currentUser
        if (user == null) {
            Log.w(TAG, "User not logged in")
            return Result.failure(Exception("User not authenticated to add skatepark."))
        }

        return try {
            val newSkatepark = Skatepark(
                name = name,
                description = description,
                location = location,
                address = address,
                creatorId = user.uid,
                creatorName = user.displayName ?: user.email // Podrías tener un campo 'username' en tu modelo User
                // createdAt se establecerá por @ServerTimestamp
                // averageRating y ratingCount se inicializan a 0.0 y 0
            )
            val documentReference = skateparksCollection.add(newSkatepark).await()
            Log.d(TAG, "Skatepark added with ID: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding skatepark", e)
            Result.failure(e)
        }
    }

    /**
     * Actualiza un skatepark existente.
     * Solo el creador debería poder actualizarlo (esto se puede reforzar con reglas de Firestore).
     */
    suspend fun updateSkatepark(skatepark: Skatepark): Result<Unit> {
        if (skatepark.id.isBlank()) {
            return Result.failure(IllegalArgumentException("Skatepark ID cannot be empty for update."))
        }
        // Podrías añadir una verificación aquí para el creatorId si es necesario, aunque las reglas de Firestore son el lugar principal.
        return try {
            skateparksCollection.document(skatepark.id).set(skatepark).await() // set sobrescribe, update actualiza campos específicos
            Log.d(TAG, "Skatepark updated: ${skatepark.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating skatepark ${skatepark.id}", e)
            Result.failure(e)
        }
    }

    /**
     * Elimina un skatepark.
     * Solo el creador debería poder eliminarlo.
     */
    suspend fun deleteSkatepark(skateparkId: String): Result<Unit> {
        if (skateparkId.isBlank()) {
            return Result.failure(IllegalArgumentException("Skatepark ID cannot be empty for deletion."))
        }
        // Podrías añadir una verificación aquí para el creatorId.
        return try {
            skateparksCollection.document(skateparkId).delete().await()
            Log.d(TAG, "Skatepark deleted: $skateparkId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting skatepark $skateparkId", e)
            Result.failure(e)
        }
    }

    // --- Funciones para Ratings (ejemplo) ---

    /**
     * Añade un rating a un skatepark.
     * Esto idealmente sería una transacción para actualizar el rating promedio y contador de forma atómica.
     */
    suspend fun addRating(skateparkId: String, ratingValue: Float, comment: String?): Result<Unit> {
        val user = auth.currentUser
        if (user == null) {
            return Result.failure(Exception("User not authenticated to add rating."))
        }
        if (skateparkId.isBlank()) {
            return Result.failure(IllegalArgumentException("Skatepark ID cannot be empty for rating."))
        }

        val skateparkDocRef = skateparksCollection.document(skateparkId)
        val newRating = Rating(
            userId = user.uid,
            userName = user.displayName ?: user.email,
            rating = ratingValue,
            comment = comment
            // ratedAt se establecerá por @ServerTimestamp
        )

        // El manejo de ratings y el cálculo del promedio puede ser complejo.
        // Opción 1: Guardar ratings en una subcolección y usar Cloud Functions para actualizar el promedio.
        // Opción 2: Guardar ratings en una subcolección y leer todos para calcular el promedio en el cliente (menos eficiente para muchos ratings).
        // Opción 3: (Más compleja con transacciones) Actualizar un array de ratings en el documento del skatepark y el promedio.

        // Ejemplo simple (usando subcolección, el promedio no se actualiza aquí directamente):
        return try {
            skateparkDocRef.collection("ratings").add(newRating).await()
            // Aquí necesitarías una lógica para recalcular y actualizar averageRating y ratingCount en el documento del Skatepark.
            // Esto es mejor hacerlo con una transacción o una Cloud Function para evitar condiciones de carrera.
            // Por simplicidad, omito la actualización del promedio aquí.
            Log.d(TAG, "Rating added for skatepark $skateparkId by user ${user.uid}")
            Result.success(Unit)
            // TODO: Implementar la actualización del averageRating y ratingCount en el Skatepark
        } catch (e: Exception) {
            Log.e(TAG, "Error adding rating for $skateparkId", e)
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los ratings para un skatepark específico.
     */
    fun getRatingsForSkatepark(skateparkId: String): Flow<List<Rating>> = callbackFlow {
        val ratingsCollection = skateparksCollection.document(skateparkId).collection("ratings")
            .orderBy("ratedAt", Query.Direction.DESCENDING)

        val listenerRegistration = ratingsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Listen failed for ratings of $skateparkId.", error)
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val ratings = snapshot.toObjects<Rating>()
                trySend(ratings).isSuccess
            }
        }
        awaitClose {
            Log.d(TAG, "Cancelling ratings listener for $skateparkId")
            listenerRegistration.remove()
        }
    }
}