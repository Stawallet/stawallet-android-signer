package io.stawallet.signer.seed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.stawallet.signer.R

class NewSeedInstructionFragment : Fragment() {

    private lateinit var viewModel: NewSeedsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(requireActivity()).get(NewSeedsViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_new_seed_instruction, container, false)



        return rootView
    }
}