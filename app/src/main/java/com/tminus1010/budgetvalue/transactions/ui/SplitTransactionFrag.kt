package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.ui.ViewItemRecipeFactoryProvider
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.middleware.unbox
import com.tminus1010.budgetvalue._core.ui.data_binding.databind
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.databinding.FragSplitTransactionBinding
import com.tminus1010.budgetvalue.transactions.CategorizeAdvancedDomain
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsAdvancedVM
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsVM
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.budgetvalue.transactions.domain.CategorizeTransactionsDomain
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.unbox
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import javax.inject.Inject

@AndroidEntryPoint
class SplitTransactionFrag : Fragment(R.layout.frag_split_transaction) {
    @Inject lateinit var categorizeTransactionsAdvancedDomain: CategorizeAdvancedDomain
    @Inject lateinit var categorizeTransactionsDomain: CategorizeTransactionsDomain
    val categorizeTransactionsVM by activityViewModels<CategorizeTransactionsVM>()
    val categoriesVM by activityViewModels<CategoriesVM>()
    val transactionsVM by activityViewModels<TransactionsVM>()
    val categorizeTransactionsAdvancedVM by viewModels<CategorizeTransactionsAdvancedVM>()
    val categorySelectionVM: CategorySelectionVM by activityViewModels()
    val vb by viewBinding(FragSplitTransactionBinding::bind)
    val viewRecipeFactories by lazy { ViewItemRecipeFactoryProvider(requireContext()) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Mediation
        Rx.combineLatest(
            categorySelectionVM.selectedCategories,
            categorizeTransactionsDomain.transactionBox.unbox().map { it.amount }
        ).take(1)
            .map { categorizeTransactionsAdvancedDomain.calcExactSplit(it.first, it.second) }
            .subscribe { it.forEach { c, a -> categorizeTransactionsAdvancedVM.rememberCA(c, a) } }
        // # TextView: amount to split
        vb.textviewAmountToSplit.databind(viewLifecycleOwner, categorizeTransactionsVM.amountToCategorize)
        // # TMTableView
        val cellRecipeFactory = viewRecipeFactories.cellRecipeFactory
        val headerRecipeFactory = viewRecipeFactories.headerRecipeFactory
        val amountRecipeFactory = viewRecipeFactories.incomingBigDecimalRecipeFactory
        val categoryAmountRecipeFactory = viewRecipeFactories.outgoingCARecipeFactory(categorizeTransactionsAdvancedVM.intentRememberCA)
        val titledDividerRecipeFactory = viewRecipeFactories.titledDividerRecipeFactory
        categorizeTransactionsAdvancedVM.transactionToPush
            .map {
                val recipes2D = listOf(
                    headerRecipeFactory.createOne2("Category")
                            + cellRecipeFactory.createOne2("Default")
                            + cellRecipeFactory.createMany(it.categoryAmounts.keys.map { it.name }),
                    headerRecipeFactory.createOne2("Amount")
                            + amountRecipeFactory.createOne2(categorizeTransactionsAdvancedVM.defaultAmount)
                            + categoryAmountRecipeFactory.createMany(it.categoryAmounts.entries.map { it.key to it.value })
                ).reflectXY()
                val dividerMap = it.categoryAmounts.keys
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to titledDividerRecipeFactory.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, and default row
                Pair(recipes2D, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipes2D, dividerMap) -> vb.tmTableView.initialize(recipes2D, true, dividerMap, 0, 1) }
    }
}