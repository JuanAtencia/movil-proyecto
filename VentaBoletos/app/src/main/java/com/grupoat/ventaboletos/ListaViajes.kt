package com.grupoat.ventaboletos

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupoat.ventaboletos.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListaViajes : AppCompatActivity() {

    private lateinit var lblNombres :TextView
    private lateinit var lblTarjeta :TextView
    private lateinit var lblOrigen :TextView
    private lateinit var lblDestino: TextView
    private lateinit var lblFechaVia:TextView
    private lateinit var lblMonto:TextView

    private lateinit var btbDetallePago: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_viajes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        asignaReferencia()
        // Obtener el usuId guardado en SharedPreferences
        val sharedPreferences = getSharedPreferences("mi_app", Context.MODE_PRIVATE)
        val usuId = sharedPreferences.getInt("usuId", -1)

        if (usuId != -1) {
            obtenerUltimoViaje(usuId)
            obtenerUltimoPago(usuId)
        } else {
            mostrarMensaje("Error: Usuario no identificado")
        }


    }
    private fun asignaReferencia(){
        lblNombres = findViewById(R.id.lblNombreTitular)
        lblTarjeta = findViewById(R.id.lblTarjeta)
        lblOrigen = findViewById(R.id.lblOrigen)
        lblDestino = findViewById(R.id.lblDestino)
        lblFechaVia = findViewById(R.id.lblFechaVia)
        lblMonto = findViewById(R.id.lblMonto)

        btbDetallePago = findViewById(R.id.btnDetallePago)

        btbDetallePago.setOnClickListener {
            val intent = Intent(this, Seleccion ::class.java)
            startActivity(intent)
        }
    }
    private fun obtenerUltimoViaje(usuId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val respuesta = RetrofitClient.webService.obtenerUltimoViaje(usuId)
            if (respuesta.isSuccessful) {
                val viaje = respuesta.body()
                Log.d("ObtenerUltimoViaje", "Datos del viaje: $viaje")
                runOnUiThread {
                    viaje?.let {
                        lblOrigen.text = it.viajeOrigen
                        lblDestino.text = it.viajeDestino
                        lblFechaVia.text = it.viajeFecha
                        lblMonto.text = it.precio.toString()
                    }
                }
            } else {
                runOnUiThread {
                    mostrarMensaje("Error al obtener los datos del viaje")
                }
            }
        }
    }
    private fun obtenerUltimoPago(usuId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val respuesta = RetrofitClient.webService.obtenerUltimoPago(usuId)
            if (respuesta.isSuccessful) {
                val pago = respuesta.body()
                Log.d("ObtenerUltimoPago", "Datos del pago: $pago")
                runOnUiThread {
                    pago?.let {
                        lblNombres.text = it.pagoNombre
                        lblTarjeta.text = it.pagoTargeta
                    }
                }
            } else {
                runOnUiThread {
                    mostrarMensaje("Error al obtener los datos del pago")
                }
            }
        }
    }


    private fun mostrarMensaje(mensaje: String) {
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar") { dialog, which -> }
        ventana.create().show()
    }
}