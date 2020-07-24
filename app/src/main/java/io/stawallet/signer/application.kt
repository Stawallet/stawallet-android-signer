package io.stawallet.signer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.room.Room
import io.stawallet.signer.data.StawalletDatabase
import io.stawallet.signer.data.sessionManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.stawallet.signer.data.okHttpClient
import io.stawallet.signer.data.stawalletDatabase

lateinit var application: Application

class Application : android.app.Application() {
    // class Application : android.app.Application(), CameraXConfig.Provider {

    init {
        application = this
    }

    companion object {
        const val VERSION_NAME = BuildConfig.VERSION_NAME
        const val VERSION_CODE = BuildConfig.VERSION_CODE
        const val APPLICATION_ID = BuildConfig.APPLICATION_ID
        const val NOTIFICATION_CHANNEL_GENERAL = "general"
        const val NOTIFICATION_CHANNEL_STICK = "stick" // Help us keep the service running
        const val NOTIFICATION_CHANNEL_ATTEMPT = "attempt" // Whenever we need user's permission
        const val NOTIFICATION_CHANNEL_ANNOUNCEMENT = "announcement"
    }

    override fun onCreate() {
        super.onCreate()

        // val lockManager = LockManager.getInstance()
        // lockManager.appLock.logoId = R.drawable.ic_launcher_foreground
        // lockManager.enableAppLock(this, CustomPinActivity::class.java)

        initializeFirebaseCrashlytics()

        // Init notification channels:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val generalChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_GENERAL,
                "General channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            generalChannel.description = "General channel"

            val stickChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_STICK,
                "Stick channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            stickChannel.description = "Stick channel"

            val attemptChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ATTEMPT,
                "Attempt channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            attemptChannel.description = "Attempt channel"

            val announcementChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ANNOUNCEMENT,
                "Announcement channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            announcementChannel.description = "Announcement channel"

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(generalChannel)
            notificationManager.createNotificationChannel(stickChannel)
            notificationManager.createNotificationChannel(attemptChannel)
            notificationManager.createNotificationChannel(announcementChannel)
        }

        stawalletDatabase = Room.databaseBuilder(
            applicationContext,
            StawalletDatabase::class.java, "stawallet_db"
        )
            .fallbackToDestructiveMigration() // FIXME: Not a great approach in production
            .build()

    }

    fun initializeFirebaseCrashlytics() {
        val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        firebaseCrashlytics.setCustomKey("build_type", BuildConfig.BUILD_TYPE)
        firebaseCrashlytics.setCustomKey("device", "android")
        firebaseCrashlytics.setCustomKey("version_code", BuildConfig.VERSION_CODE)
        firebaseCrashlytics.setCustomKey("version_name", BuildConfig.VERSION_NAME)
        if (sessionManager.isLoggedIn()) {
            firebaseCrashlytics.setUserId(sessionManager.getPayload()?.memberId.toString())
            firebaseCrashlytics.setCustomKey(
                "usr.email",
                sessionManager.getPayload()?.email.toString()
            )
            firebaseCrashlytics.setCustomKey(
                "usr.id",
                sessionManager.getPayload()?.memberId.toString()
            )
            firebaseCrashlytics.setCustomKey(
                "usr.session",
                sessionManager.getPayload()?.sessionId.toString()
            )
            firebaseCrashlytics.setCustomKey(
                "usr.device",
                sessionManager.getPayload()?.deviceUid.toString()
            )
        }
        FirebaseCrashlytics.getInstance().log("Application opened!")
    }
}

fun Context.copyToClipboard(label: String, text: String): ClipData? {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText(label, text)
    clipboard!!.setPrimaryClip(clip)
    return clip
}
