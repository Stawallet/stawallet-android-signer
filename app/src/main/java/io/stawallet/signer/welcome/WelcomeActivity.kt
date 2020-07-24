package io.stawallet.signer.welcome

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.stawallet.signer.R

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
                }
                "pin" -> {

                }
            }
        })

    }
}