package io.stawallet.signer.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import io.stawallet.signer.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SignerService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startMyOwnForeground()
        else startForeground(1, Notification())

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val channelName = "Background Signer Service"
        val chan = NotificationChannel(
            Application.NOTIFICATION_CHANNEL_STICK,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val notificationBuilder =
            NotificationCompat.Builder(this, Application.NOTIFICATION_CHANNEL_STICK)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("Stawallet Signer is running is running")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        _syncLooperRunning = true
        startSyncLooper()
        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        _syncLooperRunning = false
        val broadcastIntent = Intent()
        broadcastIntent.action = "restart-stawallet-signer"
        broadcastIntent.setClass(this, SignerRestarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    private var _syncLooperRunning = true

    private fun startSyncLooper() {
        GlobalScope.launch(Dispatchers.IO) {
            while (_syncLooperRunning) {
                // TODO: Fetch notifications
                delay(3000)
            }
        }
    }

}