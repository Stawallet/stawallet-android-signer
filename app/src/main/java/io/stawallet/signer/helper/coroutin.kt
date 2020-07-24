package io.stawallet.signer.helper

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

public fun <T> T.coRunMain(block: suspend CoroutineScope.(T) -> Any?) {
    GlobalScope.launch(Dispatchers.Main) {
        block.invoke(this@launch, this@coRunMain)
    }
}
