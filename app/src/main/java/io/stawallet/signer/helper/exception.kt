package io.stawallet.signer.helper

import com.google.firebase.crashlytics.FirebaseCrashlytics

fun <T, R> T.unjustifiedSilence(builder: T.() -> R): R? = try {
    builder.invoke(this)
} catch (e: Exception) {
    e.printStackTrace()
    try {
        FirebaseCrashlytics.getInstance().recordException(e)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    null
}
