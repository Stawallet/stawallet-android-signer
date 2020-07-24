package io.stawallet.signer.data

import android.annotation.SuppressLint
import android.preference.PreferenceManager
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.stawallet.signer.application

/**
 * Should get manually updated while login/logout
 * `null` means `not logged in`
 */

@SuppressLint("StaticFieldLeak")
object sessionManager {

    const val ADMIN = "admin"
    const val CLIENT = "client"
    const val SEMITRUSTED_CLIENT = "semitrusted_client"
    const val TRUSTED_CLIENT = "trusted_client"

    var jwtToken: String? = null
        set(value) {
            field = value
            PreferenceManager.getDefaultSharedPreferences(application.applicationContext).edit()
                .putString("jwtToken", value).apply()
        }
        get() {
            return PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                .getString("jwtToken", null)
        }

    var fbToken: String? = null
        set(value) {
            field = value
            PreferenceManager.getDefaultSharedPreferences(application.applicationContext).edit()
                .putString("fbToken", value).apply()
        }
        get() {
            return PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                .getString("fbToken", null)
        }

    fun login(t: String) {
        jwtToken = t
    }

    fun whipSession() {
        jwtToken = null
        cookieJar.clear()
        cookieJar.clearSession()
    }

    fun isLoggedIn() = jwtToken != null

    fun getPayload(): JwtPayload? =
        if (!isLoggedIn()) null
        else Gson().fromJson(
            Base64.decode(jwtToken?.split(".")?.get(1), Base64.NO_PADDING).toString(Charsets.UTF_8),
            JwtPayload::class.java
        )

    val roles: List<String> get() = getPayload()?.roles ?: emptyList()

    val isAdmin: Boolean get() = roles.contains(ADMIN)
    val isClient: Boolean get() = isSemiTrustedClient or roles.contains(CLIENT)
    val isSemiTrustedClient: Boolean get() = isTrustedClient or roles.contains(SEMITRUSTED_CLIENT)
    val isTrustedClient: Boolean get() = roles.contains(TRUSTED_CLIENT)

    fun bearerToken(): String? = jwtToken.run { "Bearer $this" }
}

data class JwtPayload(
    val sessionId: String,
    val deviceUid: String?,
    @SerializedName("id") val memberId: String,
    val email: String,
    @SerializedName("fnm") val firstName: String?,
    @SerializedName("lnm") val lastName: String?,
    @SerializedName("ioc") val isOnboardingCompleted: Boolean?,
    @SerializedName("iev") val isEvidenceVerified: Boolean?,
    val roles: List<String>
)
