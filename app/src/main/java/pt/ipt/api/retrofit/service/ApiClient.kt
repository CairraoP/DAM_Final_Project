package pt.ipt.api.retrofit.service

import okhttp3.OkHttpClient
import pt.ipt.api.model.GlobalVariables
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

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

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(GlobalVariables.CON_STRING)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authService: AuthService = retrofit.create(AuthService::class.java)
}
