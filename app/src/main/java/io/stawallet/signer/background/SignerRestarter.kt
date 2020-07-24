package io.stawallet.signer.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build


class SignerRestarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, SignerService::class.java))
        } else {
            context.startService(Intent(context, SignerService::class.java))
        }
    }
}
