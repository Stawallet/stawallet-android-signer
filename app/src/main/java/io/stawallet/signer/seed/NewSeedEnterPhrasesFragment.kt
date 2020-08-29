package io.stawallet.signer.seed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.auth0.android.jwt.JWT
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.integration.android.IntentIntegrator
import io.stawallet.signer.R
import kotlinx.android.synthetic.main.fragment_new_seed_enter_phrases.view.*
import org.kethereum.bip39.generateMnemonic
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.validate
import org.kethereum.bip39.wordlists.WORDLIST_ENGLISH
import java.lang.Exception


class NewSeedEnterPhrasesFragment : Fragment() {

    private lateinit var viewModel: NewSeedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(requireActivity()).get(NewSeedViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_new_seed_enter_phrases, container, false)

        rootView.generate_phrases.setOnClickListener {
            rootView.phrases.setText(generateMnemonic(128, WORDLIST_ENGLISH))
            rootView.generate_phrases.isEnabled = false
            MaterialAlertDialogBuilder(context)
                .setMessage("We strongly advise you to keep these words in a safe place. If you lost theme, there is NO WAY to recover them!")
                .show()
        }

        rootView.validate_phrases.setOnClickListener {
            rootView.phrases.text?.toString()?.takeIf { it.isNotEmpty() }
                ?.takeIf { MnemonicWords(it).validate(WORDLIST_ENGLISH) }
                ?.takeIf {
                    viewModel.setMnemonicPassphrase(it)
                    viewModel.changePage("examine")
                    true
                } ?: Toast.makeText(
                requireContext(),
                "The entered phrases are not valid!",
                Toast.LENGTH_LONG
            ).show()
        }

        return rootView
    }

    fun processQrCodeResult(qrcode: String?) {
        try {
            val jwt = JWT(qrcode!!)
            val claims = jwt.claims
            val publicKey: String = claims["publicKey"]?.asString()!!
            val title: String = claims["title"]?.asString()!!
            val serverPublicKey: String = claims["publicKey"]?.asString()!!

        } catch (e: Exception) {
            return Toast.makeText(
                requireContext(),
                "Invalid QrCode, please try again.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            processQrCodeResult(result.contents)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}