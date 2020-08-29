package io.stawallet.signer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.stawallet.signer.data.addMockSeeds

class NewSeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_seed)

        addMockSeeds()
//        if (AppProtection.isUnlocked.not()) {
//            finish()
//            return
//        }

        val lockFragment =
            PFFLockScreenConfiguration.Builder(this).setMode(PFFLockScreenConfiguration.MODE_AUTH)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_seeds, R.id.navigation_activity, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

}