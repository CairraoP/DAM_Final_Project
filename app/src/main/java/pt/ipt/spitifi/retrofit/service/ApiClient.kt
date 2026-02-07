package pt.ipt.spitifi.retrofit.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import pt.ipt.spitifi.model.GlobalVariables
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val gson:Gson = GsonBuilder().create()
    private val URL = GlobalVariables.CON_STRING

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val token = TokenManager.getToken()
            val newRequest = if (token != null) {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else chain.request()

            chain.proceed(newRequest)
        }
        .build()

    /*
    *
    * Retrofit para aceder aos objetos da API
    *
    */
     val retrofitAPI: Retrofit = Retrofit.Builder()
        .baseUrl(URL+"api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val albumService: AlbumService = retrofitAPI.create(AlbumService::class.java)

    val artistService: ArtistService = retrofitAPI.create(ArtistService::class.java)


    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
}
