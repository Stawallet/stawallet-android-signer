package io.stawallet.signer.welcome

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
                    val fragment = PFLockScreenFragment()
                    val builder =
                        PFFLockScreenConfiguration.Builder(this)
                            .setMode(PFFLockScreenConfiguration.MODE_CREATE)
                    fragment.setConfiguration(builder.build())
                    fragment.setCodeCreateListener(object : OnPFLockScreenCodeCreateListener {
                        override fun onNewCodeValidationFailed() {
                            TODO("Not yet implemented")
                        }

                        override fun onCodeCreated(encodedCode: String) {
                            sessionManager.pinCode = encodedCode
                            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
                        }
                    })
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, LoginFragment.newInstance())
                        .commitNow()

                }
            }
        })

    }
}