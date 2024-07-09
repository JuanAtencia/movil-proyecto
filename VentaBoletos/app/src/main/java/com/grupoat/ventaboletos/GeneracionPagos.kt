package com.grupoat.ventaboletos

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupoat.ventaboletos.entidad.Pago
import com.grupoat.ventaboletos.entidad.Usuario
import com.grupoat.ventaboletos.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeneracionPagos : AppCompatActivity() {

    private lateinit var txtTitular:EditText
    private lateinit var txtNumeroTarjeta:EditText
    private lateinit var txtCvv:EditText
    private lateinit var txtVencimiento:EditText
    private lateinit var btnAceptarPago:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_generacion_pagos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        asignarReferencia()
    }
    private fun asignarReferencia(){
        txtTitular =findViewById(R.id.txtTitular)
        txtNumeroTarjeta = findViewById(R.id.txtNumeroTarjeta)
        txtCvv = findViewById(R.id.txtCvv)
        txtVencimiento =findViewById(R.id.txtVencimiento)
        btnAceptarPago = findViewById(R.id.btnAceptarPago)

        btnAceptarPago.setOnClickListener {
            if(validarCampos()){
                agregarPago()
            }
        }

    }
    private fun agregarPago() {
        // Recuperar el usuId de SharedPreferences
        val sharedPreferences = getSharedPreferences("mi_app", Context.MODE_PRIVATE)
        val usuId = sharedPreferences.getInt("usuId", -1)  // Obtener el usuId guardado

        // Asegurarse de que el usuId es válido
        if (usuId == -1) {
            mostrarMensaje("Error: Usuario no identificado")
            return
        }

        // Crear el objeto Pago y asignar los valores
        val pago = Pago(0, "", "", "", "", 0).apply {
            pagoNombre = txtTitular.text.toString()
            pagoTargeta = txtNumeroTarjeta.text.toString()
            pagoCodigo = txtCvv.text.toString()
            pagoVencimiento = txtVencimiento.text.toString()
            this.usuId = usuId  // Asignar el usuId
        }

        // Enviar el pago al servidor usando Retrofit
        CoroutineScope(Dispatchers.IO).launch {
            val rpta = RetrofitClient.webService.agregarPago(pago)
            runOnUiThread {
                if (rpta.isSuccessful) {
                    mostrarMensaje(rpta.body().toString())
                } else {
                    mostrarMensaje("Error al registrar el pago")
                }
            }
        }
    }
    private fun validarCampos(): Boolean {
        var valid = true

        if (txtTitular.text.toString().trim().isEmpty()) {
            txtTitular.error = "Este campo es obligatorio"
            valid = false
        }

        val numeroTarjeta = txtNumeroTarjeta.text.toString().trim()
        if (numeroTarjeta.isEmpty()) {
            txtNumeroTarjeta.error = "Este campo es obligatorio"
            valid = false
        } else if (numeroTarjeta.length != 16) {
            txtNumeroTarjeta.error = "El número de tarjeta debe tener 16 caracteres"
            valid = false
        }

        val vencimiento = txtVencimiento.text.toString().trim()
        if (vencimiento.isEmpty()) {
            txtVencimiento.error = "Este campo es obligatorio"
            valid = false
        } else {
            val vencimientoPattern = Regex("^(0[1-9]|1[0-2])/\\d{2}\$")
            if (!vencimiento.matches(vencimientoPattern)) {
                txtVencimiento.error = "El formato debe ser MM/AA"
                valid = false
            }
        }

        val cvv = txtCvv.text.toString().trim()
        if (cvv.isEmpty()) {
            txtCvv.error = "Este campo es obligatorio"
            valid = false
        } else if (cvv.length != 3) {
            txtCvv.error = "El CVV debe tener 3 caracteres"
            valid = false
        }

        return valid
    }

    private fun mostrarMensaje(mensaje:String){
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(this,ListaViajes::class.java)
            startActivity(intent)
        })
        ventana.create().show()
    }
}