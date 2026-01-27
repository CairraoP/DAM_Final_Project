package pt.ipt.api.retrofit.service

import pt.ipt.api.model.Artist
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtistService {

    // Buscar um album pelo ID
    @GET("artist")
    fun getArtists(): Call<List<Artist>>

    // Buscar um album pelo ID
    @GET("artist/{username}")
    fun getArtist(@Path("username") string: String): Call<Artist>
}