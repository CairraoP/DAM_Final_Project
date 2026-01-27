package pt.ipt.api.retrofit;

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import pt.ipt.api.model.GlobalVariables
import pt.ipt.api.retrofit.service.AlbumService
import pt.ipt.api.retrofit.service.ApiClient.client
import pt.ipt.api.retrofit.service.ArtistService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {
    private val gson:Gson = GsonBuilder().setLenient().create()

    private val URL = GlobalVariables.CON_STRING
    /*
    *
    * Retrofit para aceder aos objetos da API
    *
    */
    private val retrofit = Retrofit.Builder()
        .baseUrl(URL+"api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun albumService() = retrofit.create(AlbumService::class.java)

    fun artistService() = retrofit.create(ArtistService::class.java)

}