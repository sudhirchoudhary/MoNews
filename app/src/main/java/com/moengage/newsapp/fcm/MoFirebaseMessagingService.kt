package com.moengage.newsapp.fcm

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.moengage.newsapp.R

class MoFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification?.let {
            //Message Services handle notification
            val notification = NotificationCompat.Builder(this)
                .setContentTitle(message.from)
                .setContentText(it.body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
            val manager = NotificationManagerCompat.from(applicationContext)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            manager.notify(0, notification)

        }
    }
    override fun onNewToken(token: String) {
        //handle token
    }
}