package com.grupoat.ventaboletos.servicio

import com.google.gson.GsonBuilder
import com.grupoat.ventaboletos.entidad.DetalleViaje
import com.grupoat.ventaboletos.entidad.Pago
import com.grupoat.ventaboletos.entidad.Usuario
import com.grupoat.ventaboletos.entidad.Viaje
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

object AppConstantes{
    const val BASE_URL = "http://192.168.18.11:4000"
}
interface WebService {
    @GET("/usuario")
    suspend fun obtenerUsuario(): Response<UsuarioResponse>

    @GET("/viaje")
    suspend fun obtenerViaje(): Response<ViajeResponse>

    @GET("/viaje/{usuId}")
        suspend fun obtenerViajesPorUsuario(@Path("usuId") usuId: Int): Response<ViajeResponse>

    @GET("/usuario/{id}")
    suspend fun buscarUsuario(@Path("id") id:Int): Response<Usuario>

    @POST("/usuario/agregar")
    suspend fun agregarUsuario(@Body usuario: Usuario): Response<String>


    @PUT("/usuario/actualizar/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Int,
        @Body usuario: Usuario
    ): Response<String>

    @POST("/usuario/login")
    suspend fun iniciarSesion(
        @Query("usuario") usuario: String,
        @Query("contrasena") contrasena: String
    ): Response<Usuario>


    @POST("/viaje/agregar")
    suspend fun agregarViaje(@Body viaje: Viaje): Response<String>

    @POST("/pago/agregar")
    suspend fun agregarPago(@Body pago: Pago): Response<String>

    @GET("/viaje/{id}")
    suspend fun buscarViaje(@Path("id") id: Int): Response<Viaje>

    @GET("/viaje/ultimo/{usuId}")
    suspend fun obtenerUltimoViaje(@Path("usuId") usuId: Int): Response<Viaje>

    @GET("/pago/ultimo/{usuId}")
    suspend fun obtenerUltimoPago(@Path("usuId") usuId: Int): Response<Pago>
}

object RetrofitClient {
    val webService: WebService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstantes.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().create())
            )
            .build().create(WebService::class.java)
    }
}