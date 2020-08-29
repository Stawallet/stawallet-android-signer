package io.stawallet.signer.seed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.stawallet.signer.R
import io.stawallet.signer.data.Seed
import io.stawallet.signer.data.SeedDao
import io.stawallet.signer.data.SeedsRepository
import kotlinx.android.synthetic.main.fragment_new_seed_examine.view.*
import kotlinx.android.synthetic.main.fragment_new_seed_review.view.*
import java.util.*


class NewSeedReviewFragment : Fragment() {

    private lateinit var viewModel: NewSeedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(requireActivity()).get(NewSeedViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_new_seed_review, container, false)

        rootView.review_text.text = ""

        rootView.review_ok.setOnClickListener {
            SeedsRepository.addNewSeed(
                Seed(

                )
            )
            viewModel.changePage("finish")
        }


        return rootView
    }

}