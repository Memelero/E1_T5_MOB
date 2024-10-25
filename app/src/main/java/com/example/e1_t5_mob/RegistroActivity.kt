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
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroActivity : AppCompatActivity() {

    private lateinit var nombreEditText: EditText
    private lateinit var direccionEditText: EditText
    private lateinit var fechaEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registrarButton: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var respuestaTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Referencias a los elementos del layout
        nombreEditText = findViewById(R.id.editTextText)
        direccionEditText = findViewById(R.id.editTextText2)
        fechaEditText = findViewById(R.id.editTextdate)
        emailEditText = findViewById(R.id.editTextTextEmailAddress)
        passwordEditText = findViewById(R.id.editTextNumberPassword)
        registrarButton = findViewById(R.id.button2)
        respuestaTextView = findViewById(R.id.idTextView6)

        registrarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString().trim()
            val apellido = direccionEditText.text.toString().trim()
            val fechaNacimiento = fechaEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (nombre.isEmpty() || apellido.isEmpty() || fechaNacimiento.isEmpty() || email.isEmpty() || password.isEmpty()) {
                respuestaTextView.text = "Por favor, completa todos los campos"
                respuestaTextView.setTextColor(Color.RED)
            } else {
                registrarUsuarioFirestore(nombre, apellido, fechaNacimiento, email, password)
            }
        }
    }

    private fun registrarUsuarioFirestore(nombre: String, apellido: String, fechaNacimiento: String, email: String, password: String) {
        val user = hashMapOf(
            "nombre" to nombre,
            "apellido" to apellido,
            "fnacimiento" to fechaNacimiento,
            "email" to email,
            "contraseña" to password
        )

        val usuarioDocRef = db.collection("usuarios").document(email)  // Definir la referencia del documento

        usuarioDocRef.get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    usuarioDocRef.set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()

                            guardarSesion(email, nombre, apellido , fechaNacimiento)

                            // Obtener la fecha actual
                            val fechaActual = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                            // Crear workout inicial con la fecha actual
                            val workout = hashMapOf(
                                "nombre" to "Entrenamiento inicial",
                                "tiempo_realizado" to "0 min",
                                "tiempo_estimado" to "30 min",
                                "fecha" to fechaActual,
                                "porcent_ejer" to "0%",
                                "url" to "http://ejemplo.com",
                                "nivel" to "Principiante"
                            )

                            // Añadir el workout inicial a la subcolección 'historial_workouts' dentro del documento del usuario
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

    private fun crearHistorialDeWorkouts(userId: String) {
        val workoutHistory = hashMapOf(
            "userId" to userId,
            "workouts" to listOf<Map<String, Any>>() // Lista inicial de workouts vacía
        )

        db.collection("workouts").document(userId)
            .set(workoutHistory)
            .addOnSuccessListener {
                Log.d("RegistroActivity", "Historial de workouts creado exitosamente para el usuario con ID: $userId")
            }
            .addOnFailureListener { e ->
                Log.w("RegistroActivity", "Error al crear historial de workouts", e)
            }
    }

    private fun guardarSesion(email: String, nombre: String, apellido: String, fechaNacimiento: String) {
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("email", email)
        editor.putString("nombre", nombre)
        editor.putString("apelido", apellido)
        editor.putString("fnacimiento", fechaNacimiento) // Guardar fecha como String

        // Confirmar que los datos se guardan
        editor.apply()

        // Añadir logs para verificar los valores almacenados
        Log.d("GuardarSesion", "Email: $email")
        Log.d("GuardarSesion", "Nombre: $nombre")
        Log.d("GuardarSesion", "Direccion: $apellido")
        Log.d("GuardarSesion", "Fecha de Nacimiento: $fechaNacimiento")
    }
}
