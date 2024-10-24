package com.example.e1_t5_mob

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Locale

class PerfilActivity : AppCompatActivity() {

    private lateinit var textViewNombre: TextView
    private lateinit var textViewEmail: TextView
    private lateinit var textViewApellido: TextView
    private lateinit var textViewFechaNacimiento: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Asignar las vistas
        textViewNombre = findViewById(R.id.textViewNombre)
        textViewApellido = findViewById(R.id.textViewApellido)
        textViewEmail = findViewById(R.id.textViewEmail)
        textViewFechaNacimiento = findViewById(R.id.textViewFechaNacimiento)

        // Obtener datos de la sesión
        obtenerDatosSesion()
    }

    private fun obtenerDatosSesion() {
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val nombre = sharedPreferences.getString("nombre", null)
        val apellido = sharedPreferences.getString("apellido", null)
        val email = sharedPreferences.getString("email", null)
        val fnacimiento = sharedPreferences.getString("fnacimiento", null)

        // Mostrar datos
        Log.d("GuardarSesion", "Nombre: $nombre")
        Log.d("GuardarSesion", "Apellido: $apellido")
        Log.d("GuardarSesion", "Email: $email")
        Log.d("GuardarSesion", "Fecha de Nacimiento: $fnacimiento")

        // Mostrar datos
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
}
