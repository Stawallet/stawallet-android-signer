package io.stawallet.signer.seed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.stawallet.signer.data.Listing
import io.stawallet.signer.data.Seed
import io.stawallet.signer.data.SeedsRepository

class SeedsViewModel : ViewModel() {

    val seedsListing by lazy { SeedsRepository.getAllSeeds() }
}