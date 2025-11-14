package pt.ipt.api.retrofit;

import android.os.Build
import pt.ipt.api.retrofit.service.AlbumService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.internal.GsonBuildConfig
import okhttp3.OkHttpClient
import pt.ipt.api.model.GlobalVariables
import pt.ipt.api.retrofit.service.ApiClient
import pt.ipt.api.retrofit.service.ApiClient.client
import pt.ipt.api.retrofit.service.AuthService
import pt.ipt.api.retrofit.service.TokenManager

class RetrofitInitializer {
    private val gson:Gson = GsonBuilder().setLenient().create()

    private val API = GlobalVariables.CON_STRING
    /*
    *
    * Retrofit para aceder aos objetos da API
    *
    */
    private val retrofit = Retrofit.Builder()
        .baseUrl(API+"api/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun albumService() = retrofit.create(AlbumService::class.java)
}