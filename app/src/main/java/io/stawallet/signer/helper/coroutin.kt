package io.stawallet.signer.helper

import kotlinx.coroutines.*

public fun <T> T.coRunMain(block: suspend CoroutineScope.(T) -> Any?) {
    GlobalScope.launch(Dispatchers.Main) {
        block.invoke(this@launch, this@coRunMain)
    }
}


suspend fun <T, R> T.runWithMinimumDelay(
    delayMillis: Int,
    block: suspend T.() -> R
): R {
    val startTime = System.currentTimeMillis()
    val result = block.invoke(this@runWithMinimumDelay)
    val delaySoFar = System.currentTimeMillis() - startTime
    if (delaySoFar < delayMillis) delay(delayMillis - delaySoFar)
    return result
}
