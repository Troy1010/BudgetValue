package com.tminus1010.budgetvalue.transactions.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.all_features.data.service.MoshiProvider.moshi
import com.tminus1010.budgetvalue.all_features.framework.view.viewBinding
import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.databinding.FragTransactionBinding
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.data.TransactionDTO
import com.tminus1010.budgetvalue.transactions.presentation.TransactionVM
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.misc.extensions.fromJson
import com.tminus1010.tmcommonkotlin.misc.extensions.toJson
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TransactionFrag : Fragment(R.layout.frag_transaction) {
    private val vb by viewBinding(FragTransactionBinding::bind)
    private val transactionVM by viewModels<TransactionVM>()

    @Inject
    lateinit var categoryAmountsConverter: CategoryAmountsConverter
    private val transaction by lazy { Transaction.fromDTO(moshi.fromJson<TransactionDTO>(requireArguments().getString(KEY1))!!, categoryAmountsConverter) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Setup
        transactionVM.transaction.onNext(transaction)
        // # Events
        transactionVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        transactionVM.toast.observe(viewLifecycleOwner) { easyToast(it) }
        // # State
        vb.buttonsview.buttons = transactionVM.buttons
        vb.tmTableViewTitle.bind(transactionVM.upperRecipeGrid) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(requireContext()) } },
                shouldFitItemWidthsInsideTable = true,
            )
        }
        vb.tmTableView.bind(transactionVM.lowerRecipeGrid) {
            initialize(
                recipeGrid = it.map { it.map { it.toViewItemRecipe(requireContext()) } },
                shouldFitItemWidthsInsideTable = true,
            )
        }
    }

    companion object {
        const val KEY1 = "KEY1"
        fun navTo(nav: NavController, transaction: Transaction, categoryAmountsConverter: CategoryAmountsConverter) {
            nav.navigate(R.id.transactionFrag, Bundle().apply {
                putString(KEY1, moshi.toJson(transaction.toDTO(categoryAmountsConverter)))
            })
        }
    }
}