package io.stawallet.signer.seed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.kethereum.bip39.entropyToMnemonic
import org.kethereum.bip39.generateMnemonic
import org.kethereum.bip39.wordlists.WORDLIST_ENGLISH

class NewSeedsViewModel : ViewModel() {


    val MNEMONIC_PHRASE_ENTROPY_SIZE = 2048

    val currentPage = MutableLiveData<String>()
    val mnemonicPassphrase = MutableLiveData<List<String>>()
    val mnemonicPassphraseEntropy = MutableLiveData<ByteArray>()
    val mnemonicPassphraseEntropyPercentage = MutableLiveData<Int>()
        .also { it.postValue(0) }

    fun changePage(page: String) {
        currentPage.postValue(page)
    }

    fun appendMnemonicEntropy(sparkle: ByteArray) {
        if (mnemonicPassphraseEntropy.value?.size ?: 0 >= MNEMONIC_PHRASE_ENTROPY_SIZE) {
            mnemonicPassphraseEntropyPercentage.postValue(100)
        } else {
            mnemonicPassphraseEntropy.postValue(mnemonicPassphraseEntropy.value?.plus(sparkle))
            mnemonicPassphraseEntropyPercentage.postValue(
                mnemonicPassphraseEntropy.value?.size?.div(MNEMONIC_PHRASE_ENTROPY_SIZE)
                    ?.times(100F)?.toInt()
            )
        }
    }

    fun progressGeneratingMnemonicPassphrase(entropy: ByteArray) =
        entropyToMnemonic(entropy, WORDLIST_ENGLISH).split(" ").let {
            mnemonicPassphrase.postValue(it)
        }

    fun generateMnemonicPassphraseOneshot() =
        generateMnemonic(128, WORDLIST_ENGLISH).split(" ").let {
            mnemonicPassphrase.postValue(it)
            mnemonicPassphraseEntropyPercentage.postValue(100)
        }

    fun setMnemonicPassphrase(p: String) {
        mnemonicPassphrase.postValue(p.split(" "))
        mnemonicPassphraseEntropyPercentage.postValue(100)
    }

}