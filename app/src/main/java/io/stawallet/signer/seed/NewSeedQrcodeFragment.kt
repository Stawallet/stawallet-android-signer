package io.stawallet.signer.seed

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.auth0.android.jwt.JWT
import com.google.zxing.integration.android.IntentIntegrator
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import io.stawallet.signer.R
import java.lang.Exception


class NewSeedQrCodeFragment : Fragment() {

    private lateinit var viewModel: NewSeedsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProviders.of(requireActivity()).get(NewSeedsViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_new_seed_qrcode, container, false)

        rootView.setOnClickListener {
            Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */
                        val integrator =
                            IntentIntegrator.forSupportFragment(this@NewSeedQrCodeFragment)
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
                        integrator.setPrompt("Scan a barcode")
                        integrator.setCameraId(0) // Use a specific camera of the device

                        integrator.setBeepEnabled(false)
                        integrator.setBarcodeImageEnabled(true)
                        integrator.initiateScan()

                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Toast.makeText(
                            requireContext(),
                            "We can not go further without camera permission!",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()

        }


        return rootView
    }

    fun processQrCodeResult(qrcode: String?) {
        try {
            val jwt = JWT(qrcode!!)
            val claims = jwt.claims
            val publicKey: String = claims["publicKey"]?.asString()!!
            val title: String = claims["title"]?.asString()!!
            val serverPublicKey: String = claims["publicKey"]?.asString()!!

        } catch (e: Exception) {
            return Toast.makeText(
                requireContext(),
                "Invalid QrCode, please try again.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        val result =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            processQrCodeResult(result.contents)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}