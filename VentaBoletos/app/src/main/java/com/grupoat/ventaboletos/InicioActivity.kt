package com.grupoat.ventaboletos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InicioActivity : AppCompatActivity() {

    private lateinit var btnInciarSesion:Button
    private lateinit var btnRegistrarUsu:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        asignarReferencia()
    }
    private fun asignarReferencia(){
        btnInciarSesion = findViewById(R.id.btnIniciarSesion)
        btnRegistrarUsu = findViewById(R.id.btnRegistrarUsu)

        btnInciarSesion.setOnClickListener {
            val intent = Intent(this, MainActivity ::class.java)
            startActivity(intent)
        }
        btnRegistrarUsu.setOnClickListener {
            val intent = Intent(this, RegistroUsuario ::class.java)
            startActivity(intent)
        }
    }
}