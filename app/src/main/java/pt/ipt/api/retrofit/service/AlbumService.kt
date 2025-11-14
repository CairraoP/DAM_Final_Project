package pt.ipt.api.retrofit.service

import pt.ipt.api.model.Album
import retrofit2.Call
import retrofit2.http.GET

interface AlbumService {
    @GET("album")
    fun list(): Call<List<Album>>

    /* @FormUrlEncoded
     @POST("API/addNote.php")
     fun addNote(@Field("title") title: String?, @Field("description") description: String?): Call<APIResult>
     */
}