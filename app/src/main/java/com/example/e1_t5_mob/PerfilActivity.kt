package com.example.e1_t5_mob

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class PerfilActivity : AppCompatActivity() {

    private lateinit var textViewNombre: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewApellido: TextView
    private lateinit var textViewFechaNacimiento: TextView
    private lateinit var buttonCerrar: Button
    private lateinit var buttonColor: Button
    private lateinit var buttonWorkouts: Button
    private lateinit var textViewIdioma: TextView
    private lateinit var buttonItzuli: Button

    private var isFondoClaro = true
    private var isEuskera = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE)
        val idiomaGuardado = sharedPreferences.getString("idioma", "es")  // Valor predeterminado 'es'
        cambiarIdioma(idiomaGuardado!!)


        textViewNombre = findViewById(R.id.textViewNombre)
        textViewApellido = findViewById(R.id.textViewApellido)
        textViewEmail = findViewById(R.id.textViewEmail)
        textViewFechaNacimiento = findViewById(R.id.textViewFechaNacimiento)
        buttonCerrar = findViewById(R.id.buttonCerrar)
        buttonColor = findViewById(R.id.buttonColor)
        buttonWorkouts = findViewById(R.id.buttonWorkouts)
        textViewIdioma = findViewById(R.id.textViewIdioma)


        obtenerDatosSesion()

        buttonWorkouts.setOnClickListener {
            val intent = Intent(this, WorkoutActivity::class.java)
            startActivity(intent)
        }

        buttonCerrar.setOnClickListener {
            cerrarSesion()
        }

        buttonColor.setOnClickListener {
            toggleFondo()
        }
        textViewIdioma.setOnClickListener {
            if (isEuskera) {
                cambiarIdioma("es")  // Cambiar a español
            } else {
                cambiarIdioma("eu")  // Cambiar a euskera
            }
            isEuskera = !isEuskera  // Alternar idioma
        }


    }



    private fun toggleFondo() {
        val rootLayout = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.rootLayout)


        if (isFondoClaro) {
            rootLayout.setBackgroundResource(R.drawable.fondo_oscuro)

            // Cambiar color de texto a blanco
            textViewNombre.setTextColor(Color.WHITE)
            textViewApellido.setTextColor(Color.WHITE)
            textViewEmail.setTextColor(Color.WHITE)
            textViewFechaNacimiento.setTextColor(Color.WHITE)

            // Cambiar color de los títulos
            findViewById<TextView>(R.id.textViewNombreTitle).setTextColor(Color.WHITE)
            findViewById<TextView>(R.id.textViewApellidoTitle).setTextColor(Color.WHITE)
            findViewById<TextView>(R.id.textViewFechaTitle).setTextColor(Color.WHITE)
            findViewById<TextView>(R.id.textViewEmailTitle).setTextColor(Color.WHITE)

        } else {
            rootLayout.setBackgroundResource(R.drawable.fondo)

            // Cambiar color de texto a oscuro (color original)
            textViewNombre.setTextColor(Color.BLACK)
            textViewApellido.setTextColor(Color.BLACK)
            textViewEmail.setTextColor(Color.BLACK)
            textViewFechaNacimiento.setTextColor(Color.BLACK)

            // Cambiar color de los títulos de vuelta a negro
            findViewById<TextView>(R.id.textViewNombreTitle).setTextColor(Color.BLACK)
            findViewById<TextView>(R.id.textViewApellidoTitle).setTextColor(Color.BLACK)
            findViewById<TextView>(R.id.textViewFechaTitle).setTextColor(Color.BLACK)
            findViewById<TextView>(R.id.textViewEmailTitle).setTextColor(Color.BLACK)
        }

        // Cambiar el estado del fondo
        isFondoClaro = !isFondoClaro
    }

    private fun obtenerDatosSesion() {
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val nombre = sharedPreferences.getString("nombre", "No disponible")
        val apellido = sharedPreferences.getString("apellido", "No disponible")
        val email = sharedPreferences.getString("email", "No disponible")
        val fnacimiento = sharedPreferences.getString("fnacimiento", "No disponible")

        // Mostrar datos en logs y en la interfaz
        Log.d("GuardarSesion", "Nombre: $nombre")
        Log.d("GuardarSesion", "Apellido: $apellido")
        Log.d("GuardarSesion", "Email: $email")
        Log.d("GuardarSesion", "Fecha de Nacimiento: $fnacimiento")

        textViewNombre.text = "$nombre"
        textViewApellido.text = "$apellido"
        textViewEmail.text = "$email"
        textViewFechaNacimiento.text = "$fnacimiento"
        // Mostrar fecha de nacimiento si lo deseas
//fnacimiento?.let {
//    val fechaNacimiento = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.toLong())
//    textViewFechaNacimiento.text = "Fecha de Nacimiento: $fechaNacimiento"
//} ?: run {
//    textViewFechaNacimiento.text = "Fecha de Nacimiento: No disponible"
//}
    }

    private fun cerrarSesion() {
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    private fun cambiarIdioma(idioma: String) {
        // Verificar si el idioma actual es diferente al que se quiere
        val sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE)
        val idiomaGuardado = sharedPreferences.getString("idioma", "es")  // Valor predeterminado 'es'

        if (idioma != idiomaGuardado) {
            // Cambiar el idioma
            val locale = Locale(idioma)
            Locale.setDefault(locale)

            val config = resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(locale)
                createConfigurationContext(config)
            } else {
                config.locale = locale
                resources.updateConfiguration(config, resources.displayMetrics)
            }

            // Guardar el nuevo idioma en SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putString("idioma", idioma)
            editor.apply()

            // Reiniciar la actividad para aplicar los cambios de idioma
            recreate()  // Esto recargará la actividad con el nuevo idioma
        }
    }
}