package com.grupoat.ventaboletos.servicio

import com.google.gson.annotations.SerializedName
import com.grupoat.ventaboletos.entidad.Usuario

data class UsuarioResponse (
    @SerializedName("listaUsuarios") var listaUsuarios:ArrayList<Usuario>
)