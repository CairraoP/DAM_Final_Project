package pt.ipt.api.retrofit.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import pt.ipt.api.model.Album
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface AlbumService {
    @GET("album")
    fun list(): Call<List<Album>>

    // Buscar um album pelo ID
    @GET("album/{id}")
    fun getAlbum(@Path("id") id: Int): Call<Album>

    // Criar um album pelo ID
    @Multipart
    @POST("album")
    fun createAlbum(@Part("Titulo") titulo: RequestBody,
                    @Part fotoAlbum: MultipartBody.Part?,
                    @Part musicasNovas: List<MultipartBody.Part>): Call<Album>

    // Editar um album pelo ID
    @Multipart
    @PATCH("album/{id}")
    fun updateAlbum(
        @Path("id") id: Int,
        @Part("Titulo") titulo: RequestBody?,
        @Part("ArtistaUsername") artista: RequestBody?,
        @Part foto: MultipartBody.Part? // This handles the IFormFile FotoAlbum
    ): Call<Album>

    // Eliminar um album pelo ID
    @DELETE("album/{id}")
    fun deleteAlbum(@Path("id") id: Int): Call<Unit>
}