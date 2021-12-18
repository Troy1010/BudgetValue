package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.all.extensions.observe
import com.tminus1010.budgetvalue._core.data.MoshiProvider.moshi
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.databinding.FragReceiptCategorizationBinding
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.data.TransactionDTO
import com.tminus1010.budgetvalue.transactions.presentation.ReceiptCategorizationVM
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReceiptCategorizationFrag : Fragment(R.layout.frag_receipt_categorization) {
    val receiptCategorizationVM by viewModels<ReceiptCategorizationVM>()
    lateinit var vb: FragReceiptCategorizationBinding

    @Inject
    lateinit var categoryAmountsConverter: CategoryAmountsConverter
    val transaction by lazy { Transaction.fromDTO(moshi.fromJson<TransactionDTO>(requireArguments().getString(KEY1))!!, categoryAmountsConverter) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb = FragReceiptCategorizationBinding.bind(view)
        // # Setup VM
        receiptCategorizationVM.transaction.onNext(transaction)
        // # Bind Presentation Events
        receiptCategorizationVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # Bind Presentation State
        vb.textviewDescription.bind(receiptCategorizationVM.description) { text = it }
        vb.buttonsview.bind(receiptCategorizationVM.buttons) { buttons = it }
    }

    companion object {
        private const val KEY1 = "KEY1"
        fun navTo(nav: NavController, transaction: Transaction, categoryAmountsConverter: CategoryAmountsConverter) {
            nav.navigate(
                R.id.receiptCategorizationFrag,
                Bundle().apply {
                    putString(KEY1, moshi.toJson(transaction.toDTO(categoryAmountsConverter)))
                },
            )
        }
    }
}