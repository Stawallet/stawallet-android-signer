package io.stawallet.signer.seed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.stawallet.signer.R
import kotlinx.android.synthetic.main.fragment_new_seed_instruction.view.*

class NewSeedInstructionFragment : Fragment() {

    private lateinit var viewModel: NewSeedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(requireActivity()).get(NewSeedViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_new_seed_instruction, container, false)

        rootView.instruction_continue.setOnClickListener {
            viewModel.changePage("phrases")
        }

        return rootView
    }
}