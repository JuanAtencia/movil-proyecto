package com.grupoat.ventaboletos

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupoat.ventaboletos.entidad.DetalleViaje
import com.grupoat.ventaboletos.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Seleccion : AppCompatActivity() {

    private lateinit var btnSeleccionAsi:Button
    private lateinit var btnBuscarVi:Button
    private lateinit var btnPagos:Button

    private lateinit var lblNombre:TextView
    private lateinit var lblCorreo:TextView

    private lateinit var btnPerfil:CardView
    private lateinit var btnComprar:CardView
    private lateinit var btnLista:CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seleccion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AsignarJOJOReferencia()
        val sharedPreferences = getSharedPreferences("mi_app", Context.MODE_PRIVATE)
        val usuId = sharedPreferences.getInt("usuId", -1)

        if (usuId != -1) {
            obtenerUsuario(usuId)
        } else {
            Toast.makeText(this, "No se pudo obtener el ID del usuario.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun AsignarJOJOReferencia() {

        lblNombre = findViewById(R.id.lblNombre)
        lblCorreo = findViewById(R.id.lblCorreo)
        btnPerfil = findViewById(R.id.btnEditar)

        btnComprar = findViewById(R.id.btnComprar)

        btnLista = findViewById(R.id.btnHistorial)

        btnPerfil.setOnClickListener {
            val sharedPreferences = getSharedPreferences("mi_app", Context.MODE_PRIVATE)
            val usuId = sharedPreferences.getInt("usuId", -1)  // Obtener el usuId guardado

            if (usuId != -1) {
                val intent = Intent(this@Seleccion, RegistroUsuario::class.java)
                intent.putExtra("modoEdicion", true) // Indicar que estamos en modo edición
                intent.putExtra("id", usuId) // Pasar el ID del usuario a editar
                startActivity(intent)
            } else {
                // Manejar el caso donde no se pudo obtener el usuId
                Toast.makeText(this@Seleccion, "No se pudo obtener el ID del usuario.", Toast.LENGTH_SHORT).show()
            }
        }
        btnComprar.setOnClickListener {
            val intent = Intent(this@Seleccion, Viaje ::class.java)
            startActivity(intent)
        }
        btnLista.setOnClickListener {
            val intent = Intent(this@Seleccion, HistorialActivity ::class.java)
            startActivity(intent)
        }
    }

    private fun obtenerUsuario(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val respuesta = RetrofitClient.webService.buscarUsuario(id)
            if (respuesta.isSuccessful) {
                val usuario = respuesta.body()
                Log.d("ObtenerUsuario", "Datos del usuario: $usuario")
                runOnUiThread {
                    usuario?.let {
                        lblNombre.text = it.usuNombre
                        lblCorreo.text = it.usuCorreo
                    }
                }
            } else {
                runOnUiThread {
                    mostrarMensaje("Error al obtener los datos del usuario")
                }
            }
        }
    }
    private fun guardarUsuIdEnPreferencias(userId: Int) {
        val sharedPreferences = getSharedPreferences("mi_app", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("usuId", userId)
        editor.apply()
    }
    private fun mostrarMensaje(mensaje: String) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar") { dialog, which -> }
        ventana.create().show()
    }
}