package com.grupoat.ventaboletos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.grupoat.ventaboletos.entidad.Viaje
import com.grupoat.ventaboletos.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistorialActivity : AppCompatActivity() {
    private lateinit var rvHistorial:RecyclerView
    private lateinit var imgCerrar:ImageView

    private var adaptador:AdaptadorPersonalizado = AdaptadorPersonalizado()
    private var listaViaje:ArrayList<Viaje> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        asignarReferencia()
        // Recuperar el usuId de SharedPreferences
        val sharedPreferences = getSharedPreferences("mi_app", Context.MODE_PRIVATE)
        val usuId = sharedPreferences.getInt("usuId", -1)  // Obtener el usuId guardado

        // Asegurarse de que el usuId es válido
        if (usuId == -1) {
            mostrarMensaje("Error: Usuario no identificado")
            return
        }

        cargarDatos(usuId)
    }
    private fun asignarReferencia(){
        rvHistorial = findViewById(R.id.rvHistorial)
        rvHistorial.layoutManager = LinearLayoutManager(this)
        imgCerrar = findViewById(R.id.imgCerrar)

        imgCerrar.setOnClickListener {
            finish()
        }


    }
    private fun cargarDatos(usuId: Int){
        CoroutineScope(Dispatchers.IO).launch {
            var rpt = RetrofitClient.webService.obtenerViajesPorUsuario(usuId)
            runOnUiThread {
                if (rpt.isSuccessful){
                    listaViaje = rpt.body()!!.listaViaje
                    adaptador.agregarDatos(listaViaje)
                    mostrar()
                }else{
                    Toast.makeText(this@HistorialActivity,"Error al consultar servicio",Toast.LENGTH_SHORT)
                }
            }
        }

    }
    private fun mostrar(){
        rvHistorial.adapter = adaptador
    }
    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}