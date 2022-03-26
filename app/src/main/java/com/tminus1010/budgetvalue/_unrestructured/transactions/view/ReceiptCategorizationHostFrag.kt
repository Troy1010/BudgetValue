package com.tminus1010.budgetvalue._unrestructured.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_layers.extensions.easyEmit
import com.tminus1010.budgetvalue.data.service.MoshiProvider.moshi
import com.tminus1010.budgetvalue._unrestructured.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.databinding.FragReceiptCategorizationBinding
import com.tminus1010.budgetvalue._unrestructured.transactions.app.Transaction
import com.tminus1010.budgetvalue._unrestructured.transactions.data.TransactionDTO
import com.tminus1010.budgetvalue._unrestructured.transactions.presentation.ReceiptCategorizationHostVM
import com.tminus1010.tmcommonkotlin.coroutines.extensions.observe
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReceiptCategorizationHostFrag : Fragment(R.layout.frag_receipt_categorization) {
    val receiptCategorizationVM by viewModels<ReceiptCategorizationHostVM>()
    lateinit var vb: FragReceiptCategorizationBinding

    @Inject
    lateinit var categoryAmountsConverter: CategoryAmountsConverter
    val transaction by lazy { Transaction.fromDTO(moshi.fromJson<TransactionDTO>(requireArguments().getString(KEY1))!!, categoryAmountsConverter) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReceiptCategorizationBinding.bind(view)
        // # Setup VM
        receiptCategorizationVM.transaction.value = transaction
        childFragmentManager.addOnBackStackChangedListener {
            //Notify VM of "currentFrag" view event
            receiptCategorizationVM.currentFrag.easyEmit(childFragmentManager.fragments.lastOrNull())
            //If we ever start to show an empty child fragment (b/c user pressed back button), navigate up.
            if (childFragmentManager.backStackEntryCount == 0) parentFragmentManager.popBackStack()
        }
        // # Events
        receiptCategorizationVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # State
        vb.framelayout.bind(receiptCategorizationVM.fragment) {
            childFragmentManager.beginTransaction()
                .replace(id, it)
                .addToBackStack(null)
                .commit()
        }
        vb.textviewAmountToCategorize.bind(receiptCategorizationVM.amountLeft) { text = it }
        vb.textviewDescription.bind(receiptCategorizationVM.description) { text = it }
        vb.buttonsview.bind(receiptCategorizationVM.buttons) { buttons = it }
    }

    companion object {
        private const val KEY1 = "KEY1"
        fun navTo(nav: NavController, transaction: Transaction, categoryAmountsConverter: CategoryAmountsConverter) {
            nav.navigate(
                R.id.receiptCategorizationHostFrag,
                Bundle().apply {
                    putString(KEY1, moshi.toJson(transaction.toDTO(categoryAmountsConverter)))
                },
            )
        }
    }
}