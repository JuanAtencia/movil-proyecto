package com.grupoat.ventaboletos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupoat.ventaboletos.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var btnIniciar:Button
    //private lateinit var btnRegistro:Button
    private lateinit var txtCorreoPri:EditText
    private lateinit var txtContrasenaPri:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        asignarReferencias()
    }
    private fun asignarReferencias(){
        txtCorreoPri=findViewById(R.id.txtCorreoPri)
        txtContrasenaPri=findViewById(R.id.txtContrasenaPri)
        btnIniciar=findViewById(R.id.btnIniciar)
        btnIniciar.setOnClickListener {
            iniciarSesion()
        }

        /*btnRegistro=findViewById(R.id.btnRegistro)

        btnRegistro.setOnClickListener {
            val intent = Intent(this,RegistroUsuario::class.java)
            startActivity(intent)
        }*/
    }
    private fun iniciarSesion() {
        val usuario = txtCorreoPri.text.toString()
        val contrasena = txtContrasenaPri.text.toString()

        if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.webService.iniciarSesion(usuario, contrasena)
                    if (response.isSuccessful && response.body() != null) {
                        val usuarioResponse = response.body()!!
                        val usuId = usuarioResponse.usuId  // Obtener el usuId del objeto Usuario
                        // Guardar usuId en SharedPreferences
                        val sharedPreferences = getSharedPreferences("mi_app", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putInt("usuId", usuId)
                            apply()
                        }
                        runOnUiThread {
                            val intent = Intent(this@MainActivity, Seleccion::class.java)
                            startActivity(intent)
                        }
                    } else {
                        runOnUiThread {
                            mostrarMensaje("Credenciales incorrectas. Por favor, inténtalo de nuevo.")
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        mostrarMensaje("Error: ${e.message}")
                    }
                }
            }
        } else {
            mostrarMensaje("Por favor, ingrese tanto el usuario como la contraseña.")
        }
    }


    /*private fun iniciarSesion() {
        val usuario = txtCorreoPri.text.toString()
        val contrasena = txtContrasenaPri.text.toString()

        if (usuario.isNotEmpty() && contrasena.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val rpta = RetrofitClient.webService.iniciarSesion(usuario, contrasena)
                    runOnUiThread {
                        if (rpta.isSuccessful && rpta.body() != null) {
                            val usuario = rpta.body()!!
                            // Lógica para manejar el usuario que ha iniciado sesión
                            val intent = Intent(this@MainActivity, Seleccion::class.java)
                            startActivity(intent)
                        } else {
                            mostrarMensaje("Credenciales incorrectas. Por favor, inténtalo de nuevo.")
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        mostrarMensaje("Error: ${e.message}")
                    }
                }
            }
        } else {
            mostrarMensaje("Por favor, ingrese tanto el usuario como la contraseña.")
        }
    }*/

    private fun mostrarMensaje(mensaje: String) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar") { dialog, which -> }
        ventana.create().show()
    }
}
