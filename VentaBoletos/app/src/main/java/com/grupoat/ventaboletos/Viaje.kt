package com.grupoat.ventaboletos

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.grupoat.ventaboletos.entidad.Usuario
import com.grupoat.ventaboletos.entidad.Viaje
import com.grupoat.ventaboletos.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class Viaje : AppCompatActivity() {
    private val origenDestino = arrayOf("Arequipa","Cusco","Lima")

    private val precios = mapOf(
        "Lima-Cusco" to 150.0,
        "Cusco-Lima" to 150.0,
        "Arequipa-Cusco" to 100.0,
        "Cusco-Arequipa" to 100.0,
        "Lima-Arequipa" to 120.0,
        "Arequipa-Lima" to 120.0,
    )
    private lateinit var origen: AutoCompleteTextView
    private lateinit var destino: AutoCompleteTextView

    private lateinit var lblFecha: TextInputEditText
    private val calendar = Calendar.getInstance()

    private lateinit var precioPagar: TextView

    private lateinit var btnPagar:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_viaje)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        AsignarJOJOReferencia()
    }
    private fun AsignarJOJOReferencia() {

        origen = findViewById(R.id.AutoOrigen)
        destino = findViewById(R.id.AutoDestino)
        lblFecha = findViewById(R.id.lblFecha)
        precioPagar = findViewById(R.id.txtMontoPagar)
        btnPagar = findViewById(R.id.btnPagar)

        val adapterItems = ArrayAdapter(this, android.R.layout.simple_list_item_1, origenDestino)
        origen.setAdapter(adapterItems)

        origen.setOnItemClickListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position).toString()
            updatePrice()
        }
        destino.setAdapter(adapterItems)
        destino.setOnItemClickListener { parent, view, position, id ->
            val  item = parent.getItemAtPosition(position).toString()
            updatePrice()
        }
        lblFecha.setOnClickListener {
            showDateTimePicker()
        }

        btnPagar.setOnClickListener {
            if (validarCampos()) {
                registrarViaje();
            }
            /*val intent = Intent(this, GeneracionPagos::class.java)
            startActivity(intent)*/
        }
    }
    private fun updatePrice() {
        val origen = origen.text.toString()
        val destino = destino.text.toString()
        val key = "$origen-$destino"

        val price = precios[key]
        if (price != null) {
            precioPagar.text = "Precio: S/. $price"
        } else {
            precioPagar.text = "Precio no disponible"
        }
    }
    private fun showDateTimePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                showTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateDateTimeInView()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun validarCampos(): Boolean {
        var valid = true

        if (origen.text.toString().trim().isEmpty()) {
            origen.error = "Este campo es obligatorio"
            valid = false
        }
        if (destino.text.toString().trim().isEmpty()) {
            destino.error = "Este campo es obligatorio"
            valid = false
        }
        if (lblFecha.text.toString().trim().isEmpty()) {
            lblFecha.error = "Este campo es obligatorio"
            valid = false
        }
        if (precioPagar.text.toString().trim().isEmpty()) {
            precioPagar.error = "Este campo es obligatorio"
            valid = false
        }
        return valid
    }

    private fun updateDateTimeInView() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        lblFecha.setText(sdf.format(calendar.time))
    }
    private fun registrarViaje() {
        // Recuperar el usuId de SharedPreferences
        val sharedPreferences = getSharedPreferences("mi_app", Context.MODE_PRIVATE)
        val usuId = sharedPreferences.getInt("usuId", -1)  // Obtener el usuId guardado

        // Asegurarse de que el usuId es válido
        if (usuId == -1) {
            mostrarMensaje("Error: Usuario no identificado")
            return
        }

        // Crear el objeto Viaje y asignar los valores
        val viaje = Viaje(0, "", "", "", 0, 0.0).apply {
            viajeOrigen = origen.text.toString()
            viajeDestino = destino.text.toString()
            viajeFecha = lblFecha.text.toString()
            precio = precios["$viajeOrigen-$viajeDestino"] ?: 0.0
            this.usuId = usuId  // Asignar el usuId
        }

        // Enviar el viaje al servidor usando Retrofit
        CoroutineScope(Dispatchers.IO).launch {
            val rpta = RetrofitClient.webService.agregarViaje(viaje)
            runOnUiThread {
                if (rpta.isSuccessful) {
                    mostrarMensaje(rpta.body().toString())
                } else {
                    mostrarMensaje("Error al registrar el viaje")
                }
            }
        }
    }

    private fun mostrarMensaje(mensaje:String){
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("PROCEDER AL PAGO", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(this,GeneracionPagos::class.java)
            startActivity(intent)
        })
        ventana.create().show()
    }
}