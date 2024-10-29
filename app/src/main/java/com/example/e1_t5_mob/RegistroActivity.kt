package com.example.e1_t5_mob

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RegistroActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registrarButton: Button
    private lateinit var iniciarSesionButton: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var respuestaTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Referencias a los elementos del layout
        nombreEditText = findViewById(R.id.editTextText)
        apellidoEditText = findViewById(R.id.editTextText2)
        emailEditText = findViewById(R.id.editTextTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextNumberPassword)
        registrarButton = findViewById(R.id.button2)
        respuestaTextView = findViewById(R.id.idTextView6)
        iniciarSesionButton = findViewById(R.id.button3)

        iniciarSesionButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        registrarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString().trim()
            val apellido = apellidoEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty()) {
                respuestaTextView.text = "Por favor, completa todos los campos"
                respuestaTextView.setTextColor(Color.RED)
            } else {
                registrarUsuarioFirestore(nombre, apellido, email, password)
            }
        }
    }

    private fun registrarUsuarioFirestore(nombre: String, apellido: String, email: String, password: String) {
        // Generar una fecha de nacimiento aleatoria como Timestamp
        val randomFnacimiento = generarFechaAleatoria()

        val user = hashMapOf(
            "nombre" to nombre,
            "apellido" to apellido,
            "fnacimiento" to randomFnacimiento,
            "email" to email,
            "contraseña" to password,
            "nivel" to "Principiante"
        )

        val usuarioDocRef = db.collection("usuarios").document(nombre)

        usuarioDocRef.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    usuarioDocRef.set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            guardarSesion(email, nombre, apellido, randomFnacimiento)

                            // Crear workout inicial
                            val workout = hashMapOf(
                                "nombre" to "Entrenamiento inicial",
                                "tiempo_realizado" to "0 min",
                                "tiempo_estimado" to "30 min",
                                "fecha" to Timestamp.now(),
                                "porcent_ejer" to "0%",
                                "url" to "http://ejemplo.com",
                                "nivel" to "Principiante"
                            )

                            usuarioDocRef.collection("historial_workouts")
                                .add(workout)
                                .addOnSuccessListener {
                                    Log.d("RegistroActivity", "Workout inicial agregado a historial_workouts")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("RegistroActivity", "Error al agregar workout inicial", e)
                                }

                            val intent = Intent(this, PerfilActivity::class.java)
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            Log.w("RegistroActivity", "Error al registrar usuario", e)
                            respuestaTextView.text = "Error al registrar usuario: ${e.message}"
                            respuestaTextView.setTextColor(Color.RED)
                        }
                } else {
                    respuestaTextView.text = "Este correo ya está registrado"
                    respuestaTextView.setTextColor(Color.RED)
                }
            }
            .addOnFailureListener { exception ->
                respuestaTextView.text = "Error al verificar email: ${exception.message}"
                respuestaTextView.setTextColor(Color.RED)
            }
    }

    private fun generarFechaAleatoria(): Timestamp {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        // Establece una fecha aleatoria entre 1980 y el año actual
        val randomYear = (1980..currentYear).random()
        val randomMonth = (0..11).random()
        val randomDay = (1..28).random()  // Asegura que el día esté dentro de un rango válido

        calendar.set(randomYear, randomMonth, randomDay)
        return Timestamp(calendar.time)
    }

    private fun guardarSesion(email: String, nombre: String, apellido: String, fnacimiento: Timestamp) {
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("email", email)
        editor.putString("nombre", nombre)
        editor.putString("apellido", apellido)
        editor.putString("fnacimiento", fnacimiento.toDate().toString())

        editor.apply()
    }
}
