package io.stawallet.signer.welcome

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.stawallet.signer.R
import io.stawallet.signer.helper.coRunMain
import io.stawallet.signer.helper.unjustifiedCoSilence
import io.stawallet.signer.ui.splash.SplashViewModel
import kotlinx.coroutines.delay

class SplashFragment : Fragment() {

    companion object {
        fun newInstance() = SplashFragment()
    }

    private lateinit var viewModel: WelcomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.splash_fragment, container, false)
        rootView.isClickable = true
        rootView.setOnClickListener {
            viewModel.dismissSplash()
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(WelcomeViewModel::class.java)
        coRunMain {
            unjustifiedCoSilence {
                delay(2000)
                viewModel.dismissSplash()
            }
        }
    }

}