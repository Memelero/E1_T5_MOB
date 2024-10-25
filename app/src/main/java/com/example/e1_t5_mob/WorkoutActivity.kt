package com.example.e1_t5_mob

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var textViewWorkouts: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()
        textViewWorkouts = findViewById(R.id.textViewWorkouts)

        // Obtener el historial de workouts
        obtenerHistorialWorkouts()
    }

    private fun obtenerHistorialWorkouts() {
        val emailUsuario = "f@f.com" // Cambia esto por el email del usuario registrado
        db.collection("usuarios").document(emailUsuario)
            .collection("historial_workouts")
            .get()
            .addOnSuccessListener { documents ->
                val workouts = StringBuilder() // Para concatenar todos los workouts

                for (document in documents) {
                    val nombre = document.getString("nombre") ?: "Nombre no disponible"
                    val tiempoRealizado = document.getString("tiempo_realizado") ?: "0 min"
                    val tiempoEstimado = document.getString("tiempo_estimado") ?: "0 min"
                    val fecha = document.getString("fecha") ?: "Fecha no disponible"
                    val porcentEjer = document.getString("porcent_ejer") ?: "0%"
                    val url = document.getString("url") ?: "No disponible"
                    val nivel = document.getString("nivel") ?: "No disponible"

                    // Agregar información del workout al StringBuilder
                    workouts.append("Nombre: $nombre\n")
                        .append("Tiempo Realizado: $tiempoRealizado\n")
                        .append("Tiempo Estimado: $tiempoEstimado\n")
                        .append("Fecha: $fecha\n")
                        .append("Porcentaje Ejercicio: $porcentEjer\n")
                        .append("URL: $url\n")
                        .append("Nivel: $nivel\n\n")
                }

                // Mostrar todos los workouts en el TextView
                textViewWorkouts.text = workouts.toString()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al obtener workouts: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
