package io.stawallet.signer.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.safetynet.SafetyNet
import com.scottyab.rootbeer.RootBeer
import io.stawallet.signer.R
import io.stawallet.signer.SAFETY_NET_API_KEY
import io.stawallet.signer.helper.coRunMain
import io.stawallet.signer.helper.runWithMinimumDelay
import kotlinx.android.synthetic.main.splash_fragment.view.*
import kotlinx.coroutines.delay

class SplashFragment : Fragment() {

    private var checklist: ArrayList<SplashCheck>? = null

    companion object {
        fun newInstance() = SplashFragment()
    }

    private lateinit var viewModel: WelcomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.splash_fragment, container, false)
//        rootView.isClickable = true
//        rootView.setOnClickListener {
//            viewModel.dismissSplash()
//        }
        checklist = arrayListOf(
            SplashCheck(
                rootView.check1,
                "GooglePlayService version check",
                "GooglePlayService version done",
                "GooglePlayService version failed"
            ) {
                runWithMinimumDelay(1000) {
                    GoogleApiAvailability.getInstance()
                        .isGooglePlayServicesAvailable(
                            context,
                            13000000
                        ) == ConnectionResult.SUCCESS
                }
            },
            SplashCheck(rootView.check2, "Root check", "Root check done", "Root check failed") {
                runWithMinimumDelay(1000) {
                    RootBeer(context).isRooted.not()
                }
            },
            SplashCheck(
                rootView.check3,
                "SafetyNet check",
                "SafetyNet check done",
                "SafetyNet check failed"
            ) {
                runWithMinimumDelay(1000) {
                    SafetyNet.getClient(requireContext())
                        .attest("sample-nonce".toByteArray(), SAFETY_NET_API_KEY)
                    // TODO: Implement check

//                        .addOnSuccessListener(this) {
//                            // Indicates communication with the service was successful.
//                            // Use response.getJwsResult() to get the result data.
//                        }
//                        .addOnFailureListener(this) { e ->
//                            // An error occurred while communicating with the service.
//                            if (e is ApiException) {
//                                // An error with the Google Play services API contains some
//                                // additional details.
//                                val apiException = e as ApiException
//
//                                // You can retrieve the status code using the
//                                // apiException.statusCode property.
//                            } else {
//                                // A different, unknown type of error occurred.
//                                Log.d(FragmentActivity.TAG, "Error: " + e.message)
//                            }
//                        }
                    true
                }
            }/*,
            SplashCheck(
                rootView.check4,
                "SafetyNet remote check",
                "SafetyNet remote check done",
                "SafetyNet remote check failed"
            ) {
                runWithMinimumDelay(1000) {
                    GoogleApiAvailability.getInstance()
                        .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS

                }
            }*/
            //TODO: Implement remote check
        )
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(WelcomeViewModel::class.java)
//        coRunMain {
//            unjustifiedCoSilence {
//                delay(2000)
//                viewModel.dismissSplash()
//            }
//        }

        coRunMain {
            startChecklist()
        }
    }

    private suspend fun startChecklist() {
        for (it: SplashCheck in checklist ?: emptyList<SplashCheck>()) {
            it.v.isVisible = true
            it.v.setTextColor(resources.getColor(R.color.yellow))
            it.v.text = "${it.title} ..."
            val result = it.run()
            if (result) {
                it.v.setTextColor(resources.getColor(R.color.green))
                it.v.text = it.success
            } else {
                it.v.setTextColor(resources.getColor(R.color.red))
                it.v.text = it.failure
                return
            }
        }
        delay(1000)
        viewModel.dismissSplash()
    }

}


data class SplashCheck(
    val v: TextView,
    val title: String,
    val success: String,
    val failure: String,
    val run: suspend () -> Boolean
)