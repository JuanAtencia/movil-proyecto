package com.grupoat.ventaboletos.entidad

data class Pago(
    var idPago: Int,
    var pagoNombre: String,
    var pagoTargeta: String,
    var pagoCodigo: String,
    var pagoVencimiento: String,
    var usuId:Int,
)
