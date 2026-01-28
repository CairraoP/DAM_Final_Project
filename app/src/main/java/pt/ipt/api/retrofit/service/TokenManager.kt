package pt.ipt.api.retrofit.service

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private var prefs: SharedPreferences? = null

    private var username : String? = null
    private var role : String? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {

        prefs?.edit()?.remove("jwt_token")?.commit()
        //prefs?.edit()?.clear("jwt_token")?.apply()
        prefs?.edit()?.putString("jwt_token", token)?.commit()
    }

    fun getToken(): String? = prefs?.getString("jwt_token", null)

    fun clearToken() {
        prefs?.edit()?.remove("jwt_token")?.apply()
    }

    //Função para guardar os dados do utilizador
    //Como o TokenManager é inicializado um nível acima, irá dar jeito para a criaçao dos albuns de cada user
    fun saveUsername(username: String) {
        TokenManager.username = username
    }

    fun getUsername(): String? = username


    fun saveRole(role: String) {
        TokenManager.role = role
    }

    fun getRole(): String? = role

}