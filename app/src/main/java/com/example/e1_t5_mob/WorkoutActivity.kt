package com.example.e1_t5_mob

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutActivity : AppCompatActivity() {

    private lateinit var historialTextView: TextView
    private lateinit var historialContentTextView: TextView
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)
        Log.d("GuardarSesion","pasó")
        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Referencias a los elementos del layout
        historialTextView = findViewById(R.id.historialTextView)
        historialContentTextView = findViewById(R.id.historialContentTextView)

        // Obtener email del usuario logueado
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val email = sharedPreferences.getString("email", null)

        if (email != null) {
            cargarHistorial(email)
            Log.d("GuardarSesion","pasó")
        } else {
            historialContentTextView.text = "No hay usuario logueado."
        }
    }

    private fun cargarHistorial(email: String) {
        // Acceder a la subcolección 'historial_workouts' del usuario
        db.collection("usuarios").document(email).collection("historial_workouts")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    historialContentTextView.text = "No hay workouts registrados."
                } else {
                    val sb = StringBuilder()
                    for (document in documents) {
                        val nombre = document.getString("nombre") ?: "Nombre no disponible"
                        val tiempoRealizado = document.getString("tiempo_realizado") ?: "0 min"
                        val tiempoEstimado = document.getString("tiempo_estimado") ?: "0 min"
                        val fecha = document.getString("fecha") ?: "Fecha no disponible"
                        val porcentEjer = document.getString("porcent_ejer") ?: "0%"
                        val url = document.getString("url") ?: "URL no disponible"
                        val nivel = document.getString("nivel") ?: "Nivel no disponible"

                        // Agregar workout al StringBuilder
                        sb.append("Nombre: $nombre\n")
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
