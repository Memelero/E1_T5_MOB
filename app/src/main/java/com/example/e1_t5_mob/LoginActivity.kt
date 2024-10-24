package com.example.e1_t5_mob

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.log

class LoginActivity : AppCompatActivity() {

    private lateinit var editIntroduceEmail: EditText
    private lateinit var mEditTextPass: EditText
    private lateinit var mButtonIniciarSesion: Button
    private lateinit var mTextViewRespuesta: TextView
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = FirebaseFirestore.getInstance()

        editIntroduceEmail = findViewById(R.id.editIntroduceEmail)
        mEditTextPass = findViewById(R.id.editTextText2)
        mButtonIniciarSesion = findViewById(R.id.button2)
        mTextViewRespuesta = findViewById(R.id.textViewRespuesta)

        comprobarSesionIniciada()

        mButtonIniciarSesion.setOnClickListener {
            val email = editIntroduceEmail.text.toString().trim()
            val pass = mEditTextPass.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                mTextViewRespuesta.text = "Ingrese el email y la contraseña"
                mTextViewRespuesta.setTextColor(Color.RED)
            } else {
                iniciarSesionFirestore(email, pass)
            }
        }
    }

    private fun iniciarSesionFirestore(email: String, password: String) {
        db.collection("usuarios").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val passwordFirestore = document.getString("contraseña")

                        if (password == passwordFirestore) {
                            // Obtener todos los datos del usuario
                            val nombre = document.getString("nombre")
                            val apellido = document.getString("apellido")
                            val email = document.getString("email")
                            val fnacimiento = document.getTimestamp("fnacimiento")

                            // Guardar sesión
                            guardarSesion(email!!, nombre!!, apellido!!, fnacimiento)

                            // Redirigir a la vista de perfil
                            val intent = Intent(this, PerfilActivity::class.java)
                            intent.putExtra("nombre", nombre)
                            intent.putExtra("apellido", apellido)
                            intent.putExtra("email", email)
                            intent.putExtra("fnacimiento", fnacimiento)
                            startActivity(intent)

                        } else {
                            mTextViewRespuesta.text = "Contraseña incorrecta"
                            mTextViewRespuesta.setTextColor(Color.RED)
                        }
                    }
                } else {
                    mTextViewRespuesta.text = "No se encontró una cuenta con este email"
                    mTextViewRespuesta.setTextColor(Color.RED)
                }
            }
            .addOnFailureListener { exception ->
                mTextViewRespuesta.text = "Error al obtener datos: ${exception.message}"
                mTextViewRespuesta.setTextColor(Color.RED)
            }
    }

    private fun guardarSesion(email: String, nombre: String, apellido: String, fnacimiento: com.google.firebase.Timestamp?) {
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("email", email)
        editor.putString("nombre", nombre)
        editor.putString("apellido", apellido)

        // Convertir fnacimiento a String antes de guardar
        fnacimiento?.let {
            val fnacimientoString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.toDate())
            editor.putString("fnacimiento", fnacimientoString)
            Log.d("GuardarSesion", "Fecha de Nacimiento: $fnacimientoString") // Log para verificar
        }

        // Confirmar que los datos se guardan
        editor.apply()

        // Añadir logs para verificar los valores almacenados
        Log.d("GuardarSesion", "Email: $email")
        Log.d("GuardarSesion", "Nombre: $nombre")
        Log.d("GuardarSesion", "Apellido: $apellido")
    }


    private fun comprobarSesionIniciada() {
        val sharedPreferences = getSharedPreferences("SesionUsuario", MODE_PRIVATE)
        val nombre = sharedPreferences.getString("nombre", null)
        val email = sharedPreferences.getString("email", null)

        if (!nombre.isNullOrEmpty() && !email.isNullOrEmpty()) {
            val intent = Intent(this, PerfilActivity::class.java)
            intent.putExtra("nombre", nombre)
            intent.putExtra("email", email)
            startActivity(intent)
            finish()
        } else {
            Log.d("LoginActivity", "No está autenticado")
        }
    }
}
