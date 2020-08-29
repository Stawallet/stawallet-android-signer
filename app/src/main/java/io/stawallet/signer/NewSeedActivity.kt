package io.stawallet.signer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.stawallet.signer.data.addMockSeeds
import io.stawallet.signer.seed.*

class NewSeedActivity : AppCompatActivity() {
    private lateinit var viewModel: NewSeedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_seed)

        viewModel = ViewModelProviders.of(this).get(NewSeedViewModel::class.java)

//        addMockSeeds()
//        if (AppProtection.isUnlocked.not()) {
//            finish()
//            return
//        }

        viewModel.currentPage.observe(this, Observer {
            when (it) {
                "instruction" -> NewSeedInstructionFragment()
                "phrases" -> NewSeedEnterPhrasesFragment()
                "examine" -> NewSeedExaminePhrasesFragment()
                "qrcode" -> NewSeedQrCodeFragment()
                "review" -> NewSeedReviewFragment()
                "finish" -> return@Observer finish()
                else -> throw Exception("Bad page: $it")
            }.let { f ->
                supportFragmentManager.beginTransaction().replace(R.id.container, f).commit()
            }
        })

        viewModel.currentPage.postValue("instruction")

    }

}