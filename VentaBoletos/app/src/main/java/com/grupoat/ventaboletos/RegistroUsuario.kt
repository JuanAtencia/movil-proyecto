package com.grupoat.ventaboletos

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.grupoat.ventaboletos.entidad.Usuario
import com.grupoat.ventaboletos.servicio.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistroUsuario : AppCompatActivity() {

    private lateinit var txtNombre: EditText
    private lateinit var txtApellidos: EditText
    private lateinit var txtEdad: EditText
    private lateinit var txtCorreoRe: EditText
    private lateinit var txtContraseñaRe: EditText
    private lateinit var btnAceptar: Button

    private lateinit var txtTitulo:TextView

    //private lateinit var btnEditar:CardView

    //private var adaptador:AdaptadorPersonalizado = AdaptadorPersonalizado()
    private var modificar: Boolean = false
    private var id: Int = 0

    private var modoEdicion = false
    private var idUsuario = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_usuario)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        asignarReferencias()
        //recuperarDatos()
    }
    private fun asignarReferencias() {
        txtNombre = findViewById(R.id.txtNombre)
        txtApellidos = findViewById(R.id.txtApellidos)
        txtEdad = findViewById(R.id.txtEdad)
        txtCorreoRe = findViewById(R.id.txtCorreoRe)
        txtContraseñaRe = findViewById(R.id.txtContrasenaRe)
        txtTitulo = findViewById(R.id.txtTitulo)
        btnAceptar = findViewById(R.id.btnAceptar)

        //Verificar si estamos en modo edición y obtener el ID del usuario
        modoEdicion = intent.getBooleanExtra("modoEdicion", false)
        idUsuario = intent.getIntExtra("id", 0)

        if (modoEdicion) {
            // Si estamos en modo edición, cargar los datos del usuario
            obtenerUsuario(idUsuario)
            txtTitulo.text = "Actualizar Usuario"
            btnAceptar.text = "Guardar Cambios"
        } else {
            txtTitulo.text = "Registrar Usuario"
            btnAceptar.text = "Registrar"
        }


        btnAceptar.setOnClickListener {
            if (modoEdicion) {
                actualizar();
            } else {
                if (validarCampos()) {
                    registrar();
                }
            }
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
                        txtNombre.setText(it.usuNombre)
                        txtApellidos.setText(it.usuApellido)
                        txtEdad.setText(it.usuEdad.toString())
                        txtCorreoRe.setText(it.usuCorreo)
                        txtContraseñaRe.setText(it.usuPassword)
                    }
                }
            } else {
                runOnUiThread {
                    mostrarMensaje("Error al obtener los datos del usuario")
                }
            }
        }
    }



    private fun validarCampos(): Boolean {
        var valid = true

        if (txtNombre.text.toString().trim().isEmpty()) {
            txtNombre.error = "Este campo es obligatorio"
            valid = false
        }
        if (txtApellidos.text.toString().trim().isEmpty()) {
            txtApellidos.error = "Este campo es obligatorio"
            valid = false
        }
        if (txtEdad.text.toString().trim().isEmpty()) {
            txtEdad.error = "Este campo es obligatorio"
            valid = false
        }
        if (txtCorreoRe.text.toString().trim().isEmpty()) {
            txtCorreoRe.error = "Este campo es obligatorio"
            valid = false
        }
        if (txtContraseñaRe.text.toString().trim().isEmpty()) {
            txtContraseñaRe.error = "Este campo es obligatorio"
            valid = false
        }
        return valid
    }
    private fun actualizar(){
        val usuario= Usuario(0,"","","","",0)
        usuario.usuNombre=txtNombre.text.toString()
        usuario.usuApellido=txtApellidos.text.toString()
        usuario.usuCorreo=txtCorreoRe.text.toString()
        usuario.usuPassword=txtContraseñaRe.text.toString()
        usuario.usuEdad=txtEdad.text.toString().toInt()

        CoroutineScope(Dispatchers.IO).launch {
            val rpta = RetrofitClient.webService.actualizarUsuario(idUsuario,usuario)
            runOnUiThread {
                if(rpta.isSuccessful){
                    mostrarMensajeActalizar(rpta.body().toString())
                }
            }
        }
    }

    private fun registrar(){
        val usuario= Usuario(0,"","","","",0)
        usuario.usuNombre=txtNombre.text.toString()
        usuario.usuApellido=txtApellidos.text.toString()
        usuario.usuCorreo=txtCorreoRe.text.toString()
        usuario.usuPassword=txtContraseñaRe.text.toString()
        usuario.usuEdad=txtEdad.text.toString().toInt()

        CoroutineScope(Dispatchers.IO).launch {
            val rpta = RetrofitClient.webService.agregarUsuario(usuario)
            runOnUiThread {
                if(rpta.isSuccessful){
                    mostrarMensaje(rpta.body().toString())
                }
            }
        }
    }

    private fun mostrarMensaje(mensaje:String){
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        })
        ventana.create().show()
    }
    private fun mostrarMensajeActalizar(mensaje:String){
        val ventana = AlertDialog.Builder(this)
        ventana.setTitle("Información")
        ventana.setMessage(mensaje)
        ventana.setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, which ->
            val intent = Intent(this,Seleccion::class.java)
            startActivity(intent)
        })
        ventana.create().show()
    }

}