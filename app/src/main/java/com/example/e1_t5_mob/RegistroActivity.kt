package com.example.e1_t5_mob

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val btnLogin: Button = findViewById(R.id.button3)

        btnLogin.setOnClickListener {
            // Redirigir a login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)


        }
    }
}