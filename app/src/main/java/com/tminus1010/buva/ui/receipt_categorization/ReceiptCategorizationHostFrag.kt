package com.tminus1010.buva.ui.receipt_categorization

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.squareup.moshi.Types
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.KEY2
import com.tminus1010.buva.all_layers.extensions.easyEmit
import com.tminus1010.buva.environment.MoshiProvider
import com.tminus1010.buva.databinding.FragReceiptCategorizationHostBinding
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.customviews.extensions.bind
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal

@AndroidEntryPoint
class ReceiptCategorizationHostFrag : Fragment(R.layout.frag_receipt_categorization_host) {
    lateinit var vb: FragReceiptCategorizationHostBinding
    val viewModel by viewModels<ReceiptCategorizationHostVM>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReceiptCategorizationHostBinding.bind(view)
        // # Setup VM
        childFragmentManager.addOnBackStackChangedListener {
            //Notify VM of "currentFrag" view event
            viewModel.currentFrag.easyEmit(childFragmentManager.fragments.lastOrNull())
            //If we ever start to show an empty child fragment (b/c user pressed back button), navigate up.
            if (childFragmentManager.backStackEntryCount == 0) parentFragmentManager.popBackStack()
        }
        // # Events
        viewModel.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # State
        vb.framelayout.bind(viewModel.fragment) {
            childFragmentManager.beginTransaction()
                .replace(id, it)
                .addToBackStack(null)
                .commit()
        }
        vb.textviewAmountToCategorize.bind(viewModel.amountLeft) { text = it }
        vb.textviewDescription.bind(viewModel.description) { text = it }
        vb.buttonsview.bind(viewModel.buttons) { buttons = it }
    }

    companion object {
        fun navTo(nav: NavController, transaction: Transaction) {
            nav.navigate(
                R.id.receiptCategorizationHostFrag,
                Bundle().apply {
                    putParcelable(KEY1, transaction)
                },
            )
        }

        fun navTo(nav: NavController, descriptionAndTotal: Pair<String, BigDecimal>, moshiProvider: MoshiProvider) {
            nav.navigate(
                R.id.receiptCategorizationHostFrag,
                Bundle().apply {
                    putString(KEY2, moshiProvider.moshi.adapter<Pair<String, BigDecimal>>(Types.newParameterizedType(Pair::class.java, String::class.java, BigDecimal::class.java)).toJson(descriptionAndTotal))
                },
            )
        }
    }
}