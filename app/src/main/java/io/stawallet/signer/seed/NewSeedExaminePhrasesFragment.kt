package io.stawallet.signer.seed

import android.Manifest
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import io.stawallet.signer.R
import kotlinx.android.synthetic.main.fragment_new_seed_enter_phrases.view.*
import kotlinx.android.synthetic.main.fragment_new_seed_examine.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.kethereum.bip39.generateMnemonic
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.validate
import org.kethereum.bip39.wordlists.WORDLIST_ENGLISH
import java.lang.Exception
import java.util.*


class NewSeedExaminePhrasesFragment : Fragment() {

    private lateinit var viewModel: NewSeedsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(requireActivity()).get(NewSeedsViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_new_seed_examine, container, false)

        val examinationItems = (0..(viewModel.mnemonicPassphrase.value?.size ?: 0)).shuffled()
        populateExamination(Stack<Int>().also { s -> examinationItems.forEach { s.push(it) } })

        return rootView
    }

    private fun populateExamination(remainingExaminationItems: Stack<Int>) {
        if (remainingExaminationItems.size == 0) {
            viewModel.changePage("qrcode")
            return
        }
        val n = remainingExaminationItems.pop()!!
        view?.examine_title?.text = "Please write the word number $n :"
        view?.examine_field?.setText("")
        view?.examine_button?.setOnClickListener {
            if (view?.examine_field?.text?.toString() == viewModel.mnemonicPassphrase.value?.get(n)) {
                populateExamination(remainingExaminationItems)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Wrong attempt! Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}