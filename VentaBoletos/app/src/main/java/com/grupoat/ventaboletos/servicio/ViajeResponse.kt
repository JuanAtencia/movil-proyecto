package com.grupoat.ventaboletos.servicio

import com.google.gson.annotations.SerializedName
import com.grupoat.ventaboletos.entidad.Viaje

data class ViajeResponse (
    @SerializedName("listaViaje") var listaViaje:ArrayList<Viaje>
)