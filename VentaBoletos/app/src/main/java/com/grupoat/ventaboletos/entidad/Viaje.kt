package com.grupoat.ventaboletos.entidad

import java.time.LocalDateTime

data class Viaje(
    var idViaje:Int,
    var viajeOrigen:String,
    var viajeDestino:String,
    var viajeFecha:String,
    var usuId:Int,
    var precio:Double,
)