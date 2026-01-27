package pt.ipt.api.retrofit.service

import pt.ipt.api.model.Album
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AlbumService {
    @GET("album")
    fun list(): Call<List<Album>>

    // Buscar um album pelo ID
    @GET("album/{id}")
    fun getAlbum(@Path("id") id: Int): Call<Album>

    // Criar um album pelo ID
    @POST("album")
    fun createAlbum(@Body album: Album): Response<Album>

    // Editar um album pelo ID
    @PUT("api/album/{id}")
    fun updateAlbum(@Path("id") id: Int, @Body album: Album): Response<Unit>

    // Eliminar um album pelo ID
    @DELETE("api/album/{id}")
    fun deleteAlbum(@Path("id") id: Int): Response<Unit>
}