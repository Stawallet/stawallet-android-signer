package io.stawallet.signer.seed

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import io.stawallet.signer.NewSeedActivity
import io.stawallet.signer.R
import kotlinx.android.synthetic.main.fragment_seeds.view.*

class SeedListFragment : Fragment() {
    private lateinit var seedViewModel: SeedsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        seedViewModel =
            ViewModelProviders.of(requireActivity()).get(SeedsViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_seeds, container, false)

        rootView.seed_list.layoutManager = LinearLayoutManager(context)
        seedViewModel.seedsListing.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            rootView.seed_list.adapter = SeedListAdapter()
        })

        rootView.seed_add.setOnClickListener {
            startActivity(Intent(requireContext(), NewSeedActivity::class.java))
        }

        return rootView
    }

}