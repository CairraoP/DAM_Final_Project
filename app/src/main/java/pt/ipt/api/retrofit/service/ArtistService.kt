package pt.ipt.api.retrofit.service

import okhttp3.ResponseBody
import pt.ipt.api.model.Artist
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtistService {

    // Buscar todos os artistas
    @GET("artist")
    fun getArtists(): Call<List<Artist>>

    // Buscar um album pelo ID
    @GET("artist/{username}")
    fun getArtist(@Path("username") string: String): Call<Artist>


    // Apagar conta pelo username
    @DELETE("Authentication/{userId}")
    fun deleteAccount(@Path("userId") string: String?): Call<ResponseBody>
}