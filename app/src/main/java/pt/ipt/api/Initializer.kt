package pt.ipt.api

import android.app.Application
import pt.ipt.api.retrofit.service.TokenManager

class Initializer : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}