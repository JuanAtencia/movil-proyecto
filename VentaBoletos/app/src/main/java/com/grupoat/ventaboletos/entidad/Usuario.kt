package com.grupoat.ventaboletos.entidad

data class Usuario(
    var usuId:Int,
    var usuNombre:String,
    var usuApellido:String,
    var usuCorreo:String,
    var usuPassword:String,
    var usuEdad:Int,
)