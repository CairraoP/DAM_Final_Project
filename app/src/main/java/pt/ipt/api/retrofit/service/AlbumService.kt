package pt.ipt.api.retrofit.service

import pt.ipt.api.model.Album
import pt.ipt.api.model.Music
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumService {
    @GET("album")
    fun list(): Call<List<Album>>

    // GET a single album by its ID
    @GET("album/{id}")
    fun getAlbum(@Path("id") id: Int): Call<Album>

    // GET all musics of a specific album
    @GET("album/{id}/music")
    fun getMusicsFromAlbum(@Path("id") albumId: Int): Call<List<Music>>
}