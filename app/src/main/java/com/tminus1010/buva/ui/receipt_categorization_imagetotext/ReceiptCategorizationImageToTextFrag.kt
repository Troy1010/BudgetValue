package com.tminus1010.buva.ui.receipt_categorization_imagetotext

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.squareup.moshi.Types
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.KEY2
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.data.service.MoshiProvider
import com.tminus1010.buva.data.service.MoshiWithCategoriesProvider
import com.tminus1010.buva.databinding.FragReceiptCategorizationImagetotextBinding
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.framework.android.onDone
import com.tminus1010.tmcommonkotlin.androidx.CreateImageFile
import com.tminus1010.tmcommonkotlin.androidx.ShowAlertDialog
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.rx3.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import java.io.File
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ReceiptCategorizationImageToTextFrag : Fragment(R.layout.frag_receipt_categorization_imagetotext) {
    private lateinit var vb: FragReceiptCategorizationImagetotextBinding
    private val viewModel by viewModels<ReceiptCategorizationImageToTextVM>()
    private val createImageFile by lazy { CreateImageFile(requireActivity().application) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReceiptCategorizationImagetotextBinding.bind(view)
        viewModel.showAlertDialog.onNext(ShowAlertDialog(requireActivity()))
        // # User Intents
        vb.imageviewPartOfReceipt.setOnClickListener { askCameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
        // # Events
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # State
        if (latestImageUri != null)
            vb.imageviewPartOfReceipt.setImageURI(latestImageUri)
        else
            vb.imageviewPartOfReceipt.setImageResource(R.drawable.camera)
        vb.textviewReadout.movementMethod = LinkMovementMethod.getInstance()
        vb.textviewReadout.bind(viewModel.readoutText) { text = it; invalidate() } // TODO: invalidate() might not be necessary
        vb.edittextReceipt.bind(viewModel.receiptText) {
            setText(it)
            invalidate()
            Observable.interval(50, TimeUnit.MILLISECONDS)
                .take(10)
                .observe(viewLifecycleOwner) { scrollTo(0, ((lineCount - 1) * lineHeight).logx("scrollTo")) }
        } // TODO: invalidate() might not be necessary
        vb.edittextReceipt.onDone { logz("edittextReceipt.onDone"); viewModel.newReceiptText(it) }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    private val askCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {
        if (it)
            takeImageLauncher.launch(uriFromFile(createImageFile().also { latestImageFile = it }).also { latestImageUri = it })
        else
            easyToast("Camera permission is required for this feature")
    }

    private val takeImageLauncher = registerForActivityResult(ActivityResultContracts.TakePicture())
    {
        if (it) {
            vb.imageviewPartOfReceipt.setImageURI(latestImageUri)
            viewModel.newImage(latestImageFile!!)
        }
    }

    private fun uriFromFile(file: File): Uri {
        return FileProvider.getUriForFile(requireContext(), "com.tminus1010.buva.provider", file)
    }

    companion object {
        private var latestImageUri: Uri? = null
        private var latestImageFile: File? = null
        fun navTo(nav: NavController, transaction: Transaction, moshiWithCategoriesProvider: MoshiWithCategoriesProvider) {
            nav.navigate(
                R.id.receiptCategorizationImageToTextFrag,
                Bundle().apply {
                    putString(KEY1, moshiWithCategoriesProvider.moshi.toJson(transaction))
                },
            )
        }

        fun navTo(nav: NavController, descriptionAndTotal: Pair<String, BigDecimal>, moshiProvider: MoshiProvider) {
            nav.navigate(
                R.id.receiptCategorizationImageToTextFrag,
                Bundle().apply {
                    putString(KEY2, moshiProvider.moshi.adapter<Pair<String, BigDecimal>>(Types.newParameterizedType(Pair::class.java, String::class.java, BigDecimal::class.java)).toJson(descriptionAndTotal))
                },
            )
        }
    }
}