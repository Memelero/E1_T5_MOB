package com.example.e1_t5_mob

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import android.webkit.WebViewClient
import com.bumptech.glide.Glide

class WorkoutActivity : AppCompatActivity() {

    private lateinit var historialTableLayout: TableLayout
    private lateinit var db: FirebaseFirestore
    private var customFont: Typeface? = null
    private lateinit var videoWebView: WebView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()
        historialTableLayout = findViewById(R.id.historialTableLayout)

        customFont = ResourcesCompat.getFont(this, R.font.agdasima_normal)


        // Inicializa el WebView
        videoWebView = findViewById(R.id.videoWebView)

        // Habilita JavaScript en el WebView
        val webSettings: WebSettings = videoWebView.settings
        webSettings.javaScriptEnabled = true

        // Establece el WebViewClient para manejar la carga dentro del WebView
        videoWebView.webViewClient = WebViewClient()

        // Aquí puedes colocar la URL del video de YouTube
        val youtubeUrl = "https://www.youtube.com/embed/q9b9afJ-GnA"


        // Cargar el video de YouTube
        videoWebView.loadUrl(youtubeUrl)

        val buttonPefil = findViewById<Button>(R.id.buttonPERFIL)
        buttonPefil.setOnClickListener {
            val intent = Intent(this, PerfilActivity::class.java)
            startActivity(intent)
        }

        val button6 = findViewById<Button>(R.id.button6)
        button6.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


        // Get logged-in user's name from SharedPreferences
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val nombre = sharedPreferences.getString("nombre", null)


        if (nombre != null) {
            cargarHistorial(nombre)
        } else {
            addTableRow("Error", "No hay usuario logueado.")
        }
    }

    private fun cargarHistorial(nombre: String) {
        db.collection("usuarios").document(nombre).collection("historial_workouts")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    addTableRow("Error", "No hay workouts registrados.")
                } else {
                    for (document in documents) {
                        val nombreWorkout = document.getString("nombre") ?: "N/D"
                        val tiempoRealizado = document.getString("tiempo_realizado") ?: "N/D"
                        val tiempoEstimado = document.getString("tiempo_estimado") ?: "N/D"
                        val fechaTimestamp = document.getTimestamp("fecha")
                        val porcentEjer = document.getString("porcent_ejer") ?: "0%"
                        val url = document.getString("url") ?: "N/D"
                        val nivel = document.getString("nivel") ?: "N/D"

                        val fecha = if (fechaTimestamp != null) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            sdf.format(fechaTimestamp.toDate())
                        } else {
                            "N/D"
                        }

                        // Agregar filas con datos de workout
                        addStyledRow("Nombre", nombreWorkout)
                        addStyledRow("Tiempo Realizado", tiempoRealizado)
                        addStyledRow("Tiempo Estimado", tiempoEstimado)
                        addStyledRow("Fecha", fecha)
                        addStyledRow("Porcentaje Ejercicio", porcentEjer)
                        addStyledRow("URL", url)
                        addStyledRow("Nivel", nivel)


                        // Agregar separación entre workouts
                        val separator = View(this)
                        separator.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            2
                        )
                        separator.setBackgroundColor(Color.GRAY)
                        historialTableLayout.addView(separator)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("WorkoutActivity", "Error al cargar historial: ${exception.message}")
                addTableRow("Error", "Error al cargar historial.")
            }
    }



    private fun addStyledRow(title: String, value: String) {
        val row = TableRow(this)
        val rowParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        rowParams.setMargins(0, 8, 0, 8)  // Margen entre filas
        row.layoutParams = rowParams

        // Title Column
        val titleView = TextView(this)
        titleView.text = title
        titleView.textSize = 18f  // Tamaño de fuente más grande
        titleView.typeface = customFont
        titleView.setPadding(16, 16, 16, 16)  // Más padding
        titleView.setBackgroundColor(Color.parseColor("#E0E0E0"))  // Fondo gris claro para el título
        titleView.gravity = Gravity.START

        // Value Column
        val valueView = TextView(this)
        valueView.text = value
        valueView.textSize = 18f  // Tamaño de fuente más grande
        valueView.typeface = customFont
        valueView.setPadding(16, 16, 16, 16)  // Más padding
        valueView.setBackgroundColor(Color.WHITE)  // Fondo blanco para los datos
        valueView.gravity = Gravity.START

        row.addView(titleView)
        row.addView(valueView)
        historialTableLayout.addView(row)
    }

    private fun addTableRow(title: String, message: String) {
        val row = TableRow(this)
        val titleView = TextView(this)
        titleView.text = title
        titleView.setTypeface(null, Typeface.BOLD)
        row.addView(titleView)

        val messageView = TextView(this)
        messageView.text = message
        row.addView(messageView)

        historialTableLayout.addView(row)
    }
}