package pt.ipt.spitifi

import android.app.Application
import pt.ipt.spitifi.retrofit.service.TokenManager

class Initializer : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}