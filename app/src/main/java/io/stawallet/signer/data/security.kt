package io.stawallet.signer.data

import android.preference.PreferenceManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import io.stawallet.signer.application

object SecurePreference {

    init {
    }

//    val sharedPreferences by lazy {
//        val keyGenParameterSpec =
//            MasterKeys.getOrCreate(KeyGenParameterSpec.Builder("", KeyProperties.PURPOSE_ENCRYPT))
//        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
//        EncryptedSharedPreferences.create(
//            "stawallet_kyy",
//            masterKeyAlias,
//            context,
//            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//        )
//    }
//
//    val pinCode
//        get() =
}

object DeviceSecurityHelper {
    val iid: String get() = FirebaseInstanceId.getInstance().id
}


object AppProtection {

    private const val UNLOCK_TIMEOUT = 600000L
    private const val UNLOCK_MAX_TRIES = 4

    private var _pinCode: String? = null
        set(value) {
            field = value
            PreferenceManager.getDefaultSharedPreferences(application.applicationContext).edit()
                .putString("pinCode", value ?: "").apply()
        }
        get() {
            return PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                .getString("pinCode", "")
        }

    public fun validatePinCode(code: String) = _pinCode == code
    public fun getPinCode(): String? = _pinCode
    public fun setPinCode(code: String) {
        _pinCode = code
    }

    public fun isPinCodeExists(): Boolean = _pinCode.isNullOrEmpty().not()

    private var unlockedAt: Long? = null
    fun unlocked() {
        unlockedAt = System.currentTimeMillis()
        resetConsecutiveTries()
    }

    private var consecutiveTires: Int = 0
        set(value) {
            field = value
            PreferenceManager.getDefaultSharedPreferences(application.applicationContext).edit()
                .putInt("consecutiveTires", value).apply()
        }
        get() {
            return PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                .getInt("consecutiveTires", 0)
        }

    var isFingerprintEnable: Boolean = false
        set(value) {
            field = value
            PreferenceManager.getDefaultSharedPreferences(application.applicationContext).edit()
                .putBoolean("isFingerprintEnable", value).apply()
        }
        get() {
            return PreferenceManager.getDefaultSharedPreferences(application.applicationContext)
                .getBoolean("isFingerprintEnable", false)
        }

    public fun increaseConsecutiveTries() {
        consecutiveTires += 1
    }

    private fun resetConsecutiveTries() {
        consecutiveTires = 0
    }

    public fun isConsecutiveTiresExceeded() = consecutiveTires >= UNLOCK_MAX_TRIES

    val isUnlocked: Boolean
        get() = (unlockedAt == null) || (System.currentTimeMillis() - (unlockedAt
            ?: 0L) < UNLOCK_TIMEOUT)

    public fun whip() {
        unlockedAt = 0
        consecutiveTires = 0
        isFingerprintEnable = false
    }

}