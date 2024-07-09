package com.grupoat.ventaboletos.entidad

data class DetalleViaje(
    val idDetalle: Int,
    val idViaje: Int,
    val idPago: Int,
    val pagoNombre: String,
    val viajeOrigen: String,
    val viajeDestino: String,
    val viajeFecha: String,
    val precio: Double
)
