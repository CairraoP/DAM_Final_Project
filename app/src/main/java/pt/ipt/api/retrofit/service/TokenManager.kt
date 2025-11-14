package pt.ipt.api.retrofit.service

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        prefs?.edit()?.putString("jwt_token", token)?.apply()
    }

    fun getToken(): String? = prefs?.getString("jwt_token", null)

    fun clearToken() {
        prefs?.edit()?.remove("jwt_token")?.apply()
    }
}