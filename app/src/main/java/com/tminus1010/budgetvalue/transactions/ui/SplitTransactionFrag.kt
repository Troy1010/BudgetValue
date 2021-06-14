package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.add
import com.tminus1010.budgetvalue._core.extensions.add2
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.reflectXY
import com.tminus1010.budgetvalue._core.middleware.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.*
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView.ViewItemRecipeFactory
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.FragSplitTransactionBinding
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsAdvancedVM
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsVM
import com.tminus1010.budgetvalue.transactions.TransactionsVM
import com.tminus1010.budgetvalue.transactions.domain.CategorizeAdvancedDomain
import com.tminus1010.budgetvalue.transactions.domain.CategorizeTransactionsDomain
import com.tminus1010.tmcommonkotlin.core.logz
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
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
    val viewRecipeFactories by lazy { ViewItemRecipeFactoryProvider(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vb.tvAmountToSplit.bind(categorizeTransactionsVM.amountToCategorize) { text = it }
        // # Button
        vb.btnSave.setOnClickListener {
            categorizeTransactionsAdvancedVM.pushRememberedCategories()
            nav.navigateUp()
        }
        // # TMTableView
        val cellRecipeFactory = viewRecipeFactories.cellRecipeFactory
        val headerRecipeFactory = viewRecipeFactories.headerRecipeFactory
        val amountRecipeFactory = ViewItemRecipeFactory<TextView, LiveData<String>>(
            { View.inflate(context, R.layout.item_text_view, null) as TextView },
            { v, d -> v.bindIncoming(viewLifecycleOwner, d) }
        )
        val categoryAmountRecipeFactory = ViewItemRecipeFactory<EditText, Pair<Category, BigDecimal>>(
            { View.inflate(context, R.layout.item_text_edit, null) as EditText },
            { v, (category, amount) ->
                v.setText(amount.toString())
                v.onDone { categorizeTransactionsAdvancedVM.rememberCA(category, it.toMoneyBigDecimal()) }
                v.setOnCreateContextMenuListener { menu, _, _ ->
                    menu.add2(MenuItemPartial("Fill") {
                        categorizeTransactionsAdvancedVM.rememberCA(category, v.text.toString().toMoneyBigDecimal() + categorizeTransactionsAdvancedVM.defaultAmount.value!!.toBigDecimal())
                    })
                }
            }
        )
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