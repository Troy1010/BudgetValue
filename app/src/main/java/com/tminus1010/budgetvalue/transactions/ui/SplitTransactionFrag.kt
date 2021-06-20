package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.add2
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.*
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.FragSplitTransactionBinding
import com.tminus1010.budgetvalue.databinding.ItemTextEditBinding
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsAdvancedVM
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsVM
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.budgetvalue.transactions.domain.CategorizeAdvancedDomain
import com.tminus1010.budgetvalue.transactions.domain.CategorizeTransactionsDomain
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
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
    val categorizeTransactionsAdvancedVM by activityViewModels<CategorizeTransactionsAdvancedVM>()
    val vb by viewBinding(FragSplitTransactionBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.tvAmountToSplit.bind(categorizeTransactionsVM.amountToCategorize) { text = it }
        // # Button
        vb.btnSave.setOnClickListener {
            categorizeTransactionsAdvancedVM.pushRememberedCategories()
            nav.navigateUp()
        }
        // # TMTableView
        val categoryAmountRecipeFactory = ViewItemRecipeFactory3<ItemTextEditBinding, Pair<Category, BigDecimal>>(
            { ItemTextEditBinding.inflate(LayoutInflater.from(context)) },
            { (category, amount), vb, lifecycle ->
                vb.editText.setText(amount.toString())
                vb.editText.onDone { categorizeTransactionsAdvancedVM.rememberCA(category, it.toMoneyBigDecimal()) }
                vb.editText.setOnCreateContextMenuListener { menu, _, _ ->
                    menu.add2(MenuItemPartial("Fill") {
                        categorizeTransactionsAdvancedVM.rememberCA(category, vb.editText.text.toString().toMoneyBigDecimal() + categorizeTransactionsAdvancedVM.defaultAmount.value!!.toBigDecimal())
                    })
                }
            }
        )
        categorizeTransactionsAdvancedVM.transactionToPush
            .map {
                val recipes2D = listOf(
                    listOf(itemHeaderBindingRF.createOne("Category"))
                            + itemTextViewBindingRF.createOne("Default")
                            + itemTextViewBindingRF.createMany(it.categoryAmounts.keys.map { it.name }),
                    listOf(itemHeaderBindingRF.createOne("Amount"))
                            + itemTextViewBindingLRF.createOne(categorizeTransactionsAdvancedVM.defaultAmount)
                            + categoryAmountRecipeFactory.createMany(it.categoryAmounts.entries.map { it.key to it.value })
                ).reflectXY()
                val dividerMap = it.categoryAmounts.keys
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to itemTitledDividerBindingRF.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, and default row
                Pair(recipes2D, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipes2D, dividerMap) ->
                vb.tmTableView.initialize(
                    recipeGrid = recipes2D, true, dividerMap, 0, 1)
            }
    }
}