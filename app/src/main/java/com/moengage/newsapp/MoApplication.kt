package com.moengage.newsapp

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import com.moengage.newsapp.util.log

class MoApplication : Application() {
    private var httpHandler: HttpHandler? = null

    override fun onCreate() {
        super.onCreate()
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            //log("fcm token is $it")
        }
    }

    /**
     * provides the singleton object of HttpHandler
     */
    fun getHttpHandler(): HttpHandler {
        return httpHandler ?: HttpHandler()
    }
}