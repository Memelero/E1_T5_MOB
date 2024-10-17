package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.e1t5_mob.R


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


            val btnRegistro = findViewById<Button>(R.id.btnRegistro)

            btnRegistro.setOnClickListener {
                val intent = Intent(this, RegistroActivity::class.java)
                startActivity(intent)

            val loginButton = findViewById<Button>(R.id.btnLogin)

                loginButton.setOnClickListener {
                    Toast.makeText(this, "Login button clicked", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
