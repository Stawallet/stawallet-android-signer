package io.stawallet.signer.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private val userDao: UserDao = stawalletDatabase.userDao

fun <T> ListingDataSourceFactory<T>.listing(): Listing<T> {

    val livePagedList = toLiveData(
        pageSize = 20 // Doesn't matter, because server will set it
    )

    val refreshState = Transformations.switchMap(sourceLiveData) {
        (it as ListingPageKeyedDataSource<T>).initialLoad
    }

    return Listing(
        pagedList = livePagedList,
        networkState = Transformations.switchMap(sourceLiveData) {
            (it as ListingPageKeyedDataSource<T>).networkState
        },
        retry = {
            (sourceLiveData.value as ListingPageKeyedDataSource<T>?)?.retryAllFailed()
        },
        refresh = {
            (sourceLiveData.value as ListingPageKeyedDataSource<T>?)?.invalidate()
        },
        refreshState = refreshState
    )
}

object SeedsRepository {
    private val seedDao: SeedDao = stawalletDatabase.seedDao

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun getMe(): LiveData<List<Seed>> {
        return seedDao.loadAll()
    }

}

object UserRepository {
    private val userDao: UserDao = stawalletDatabase.userDao

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun getMe(): LiveData<User> {
        UserRepository.refreshMe()
        // return UserRepository.userDao.loadByEmail(sessionManager.getPayload()?.email ?: "")
        return UserRepository.userDao.loadTheOnlyOne() // FIXME
    }

    fun getUser(email: String): LiveData<User> {
        UserRepository.refreshMe()
        return UserRepository.userDao.loadByEmail(email)
    }


    fun refreshMe() {
        scope.launch {
            try {
                stawalletApiClient.me().await()
                    .apply { userDao.deleteAllBut(listOf(email)) }
                    .let { userDao.save(it) }
            } catch (e: Exception) {
                // TODO: Show error
                e.printStackTrace()
            }
        }
    }


}
