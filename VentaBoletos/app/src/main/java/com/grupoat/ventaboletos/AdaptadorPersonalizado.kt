package com.grupoat.ventaboletos

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.grupoat.ventaboletos.entidad.Usuario
import com.grupoat.ventaboletos.entidad.Viaje

class AdaptadorPersonalizado:RecyclerView.Adapter<AdaptadorPersonalizado.MiViewHolder>() {

    private var listaViajes:ArrayList<Viaje> = ArrayList()
    private lateinit var context: Context
    private var onClickDeleteItem:((Viaje) -> Unit)? = null

    fun setOnClickDeleteItem(callback:(Viaje)->Unit){
        this.onClickDeleteItem = callback
    }

    fun agregarDatos(items: ArrayList<Viaje>){
        this.listaViajes = items
    }

    fun contexto(context: Context){
        this.context = context
    }

    class MiViewHolder(var view: View):RecyclerView.ViewHolder(view) {
        private var filaOrigen = view.findViewById<TextView>(R.id.filaOrigen)
        private var filaDetino = view.findViewById<TextView>(R.id.filaDestino)
        private var filaFecha = view.findViewById<TextView>(R.id.filaFecha)
        private var filaMonto = view.findViewById<TextView>(R.id.filaMonto)

        fun setValores(viaje: Viaje){
            filaOrigen.text  = viaje.viajeOrigen
            filaDetino.text = viaje.viajeDestino
            filaFecha.text = viaje.viajeFecha
            filaMonto.text = viaje.precio.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int) = MiViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.fila,parent,false)
    )

    override fun onBindViewHolder(holder: AdaptadorPersonalizado.MiViewHolder, position: Int) {
        val viajeItem = listaViajes[position]
        holder.setValores(viajeItem)

    }

    override fun getItemCount(): Int {
        return listaViajes.size
    }
}