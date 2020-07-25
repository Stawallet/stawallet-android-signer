package io.stawallet.signer.welcome

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment.OnPFLockScreenCodeCreateListener
import io.stawallet.signer.FORGET_PASSWORD_WEB_APP_URL
import io.stawallet.signer.MainActivity
import io.stawallet.signer.R
import io.stawallet.signer.USER_REGISTER_WEB_APP_URL
import io.stawallet.signer.data.AppProtection
import io.stawallet.signer.data.sessionManager


class WelcomeActivity : AppCompatActivity() {
    private lateinit var viewModel: WelcomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_activity)
        viewModel = ViewModelProviders.of(this).get(WelcomeViewModel::class.java)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SplashFragment.newInstance())
                .commitNow()
        }

        viewModel.currentPage.observe(this, Observer {
            when (it) {
                "login" -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, LoginFragment.newInstance())
                        .commitNow()
                }
                "register" -> {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(USER_REGISTER_WEB_APP_URL)
                        )
                    )
                }
                "forget" -> {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(FORGET_PASSWORD_WEB_APP_URL)
                        )
                    )
                }
                "pin" -> {
                    val builder =
                        PFFLockScreenConfiguration.Builder(this)
                            .setTitle("Create Code")
                            .setCodeLength(6)
                            .setNewCodeValidation(true)
                            .setNewCodeValidationTitle("Please input code again")
                            .setUseFingerprint(AppProtection.isFingerprintEnable)
                    val fragment = PFLockScreenFragment()

                    fragment.setOnLeftButtonClickListener {
                        viewModel.hardLogout()
                        viewModel.currentPage.postValue("forget")
                    }

                    builder.setMode(PFFLockScreenConfiguration.MODE_CREATE)
                    fragment.setConfiguration(builder.build())
                    fragment.setCodeCreateListener(object : OnPFLockScreenCodeCreateListener {
                        override fun onNewCodeValidationFailed() {
                            Toast.makeText(
                                this@WelcomeActivity,
                                "Code does not match",
                                Toast.LENGTH_SHORT
                            ).show()
                            viewModel.hardLogout()
                            viewModel.currentPage.postValue("login")
                        }

                        override fun onCodeCreated(encodedCode: String) {
                            AppProtection.setPinCode(encodedCode)
                        }
                    })
                    fragment.setLoginListener(object :
                        PFLockScreenFragment.OnPFLockScreenLoginListener {
                        override fun onPinLoginFailed() {
                            Toast.makeText(
                                this@WelcomeActivity,
                                R.string.wrong_code,
                                Toast.LENGTH_SHORT
                            ).show()
                            AppProtection.increaseConsecutiveTries()
                            if (AppProtection.isConsecutiveTiresExceeded()) {
                                viewModel.hardLogout()
                                viewModel.currentPage.postValue("login")
                            }
                        }

                        override fun onFingerprintSuccessful() {
                            AppProtection.unlocked()
                            viewModel.currentPage.postValue("main")
                        }

                        override fun onFingerprintLoginFailed() {
                            Toast.makeText(
                                this@WelcomeActivity,
                                R.string.wrong_code,
                                Toast.LENGTH_SHORT
                            ).show()
                            AppProtection.increaseConsecutiveTries()
                            if (AppProtection.isConsecutiveTiresExceeded()) {
                                viewModel.hardLogout()
                                viewModel.currentPage.postValue("login")
                            }
                        }

                        override fun onCodeInputSuccessful() {
                            AppProtection.unlocked()
                            viewModel.currentPage.postValue("main")
                        }
                    })
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commitNow()

                }
                "unlock" -> {
                    if (AppProtection.isPinCodeExists().not()) {
                        viewModel.hardLogout()
                        viewModel.currentPage.postValue("login")
                        return@Observer
                    }
                    val builder =
                        PFFLockScreenConfiguration.Builder(this)
                            .setTitle("Unlock with your pin code or fingerprint")
                            .setCodeLength(6)
                            .setLeftButton("Can't remember")
                            .setUseFingerprint(AppProtection.isFingerprintEnable)
                    val fragment = PFLockScreenFragment()

                    fragment.setOnLeftButtonClickListener {
                        viewModel.hardLogout()
                        viewModel.currentPage.postValue("forget")
                    }

                    fragment.setConfiguration(builder.build())
                    builder.setMode(PFFLockScreenConfiguration.MODE_AUTH)
                    fragment.setLoginListener(object :
                        PFLockScreenFragment.OnPFLockScreenLoginListener {
                        override fun onPinLoginFailed() {
                            Toast.makeText(
                                this@WelcomeActivity,
                                R.string.wrong_code,
                                Toast.LENGTH_SHORT
                            ).show()
                            AppProtection.increaseConsecutiveTries()
                            if (AppProtection.isConsecutiveTiresExceeded()) {
                                viewModel.hardLogout()
                                viewModel.currentPage.postValue("login")
                            }
                        }

                        override fun onFingerprintSuccessful() {
                            AppProtection.unlocked()
                            viewModel.currentPage.postValue("main")
                        }

                        override fun onFingerprintLoginFailed() {
                            Toast.makeText(
                                this@WelcomeActivity,
                                R.string.wrong_code,
                                Toast.LENGTH_SHORT
                            ).show()
                            AppProtection.increaseConsecutiveTries()
                            if (AppProtection.isConsecutiveTiresExceeded()) {
                                viewModel.hardLogout()
                                viewModel.currentPage.postValue("login")
                            }
                        }

                        override fun onCodeInputSuccessful() {
                            AppProtection.unlocked()
                            viewModel.currentPage.postValue("main")
                        }

                    })
                    fragment.setEncodedPinCode(AppProtection.getPinCode())
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment)
                        .commitNow()

                }
                "main" -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        })

    }
}