package com.example.e1_t5_mob

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class WorkoutActivity : AppCompatActivity() {

    private lateinit var historialTextView: TextView
    private lateinit var historialContentTextView: TextView
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Referencias a los elementos del layout
        historialTextView = findViewById(R.id.historialTextView)
        historialContentTextView = findViewById(R.id.historialContentTextView)

        // Obtener email del usuario logueado
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val nombre = sharedPreferences.getString("nombre", null)

        if (nombre != null) {
            cargarHistorial(nombre)
        } else {
            historialContentTextView.text = "No hay usuario logueado."
        }
    }

    private fun cargarHistorial(nombre: String) {
        // Acceder a la subcolección 'historial_workouts' del usuario
        db.collection("usuarios").document(nombre).collection("historial_workouts")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    historialContentTextView.text = "No hay workouts registrados."
                } else {
                    val sb = StringBuilder()
                    for (document in documents) {
                        val nombre_workout = document.getString("nombre") ?: "Nombre no disponible"
                        val tiempoRealizado = document.getString("tiempo_realizado") ?: "0 min"
                        val tiempoEstimado = document.getString("tiempo_estimado") ?: "0 min"
                        val fechaTimestamp = document.getTimestamp("fecha")
                        val porcentEjer = document.getString("porcent_ejer") ?: "0%"
                        val url = document.getString("url") ?: "URL no disponible"
                        val nivel = document.getString("nivel") ?: "Nivel no disponible"

                        val fecha = if (fechaTimestamp != null) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            sdf.format(fechaTimestamp.toDate())
                        } else {
                            "Fecha no disponible"
                        }
                        // Agregar workout al StringBuilder
                        sb.append("Nombre: $nombre_workout\n")
                            .append("Tiempo Realizado: $tiempoRealizado\n")
                            .append("Tiempo Estimado: $tiempoEstimado\n")
                            .append("Fecha: $fecha\n")
                            .append("Porcentaje Ejercicio: $porcentEjer\n")
                            .append("URL: $url\n")
                            .append("Nivel: $nivel\n\n")
                    }
                    historialContentTextView.text = sb.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("WorkoutActivity", "Error al cargar historial: ${exception.message}")
                historialContentTextView.text = "Error al cargar historial."
            }
    }
}
