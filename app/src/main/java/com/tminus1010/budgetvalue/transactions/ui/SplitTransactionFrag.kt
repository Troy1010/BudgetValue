package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.categoryComparator
import com.tminus1010.budgetvalue._core.extensions.add
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.FragSplitTransactionBinding
import com.tminus1010.budgetvalue.databinding.ItemTextEditBinding
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsAdvancedVM
import com.tminus1010.budgetvalue.transactions.CategorizeTransactionsVM
import com.tminus1010.budgetvalue.transactions.domain.CategorizeAdvancedDomain
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SplitTransactionFrag : Fragment(R.layout.frag_split_transaction) {
    private val vb by viewBinding(FragSplitTransactionBinding::bind)

    @Inject
    lateinit var categorizeTransactionsAdvancedDomain: CategorizeAdvancedDomain

    @Inject
    lateinit var saveTransactionDomain: SaveTransactionDomain
    private val categorizeTransactionsVM: CategorizeTransactionsVM by activityViewModels()
    private val categorizeTransactionsAdvancedVM: CategorizeTransactionsAdvancedVM by activityViewModels()

    // # Internal
    private var _shouldIgnoreUserInputForDuration = PublishSubject.create<Unit>()
    private var shouldIgnoreUserInput = _shouldIgnoreUserInputForDuration
        .flatMap { Observable.just(false).delay(1, TimeUnit.SECONDS).startWithItem(true) }
        .startWithItem(false)
        .replay(1).autoConnect()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shouldIgnoreUserInput.observe(viewLifecycleOwner) {}
        vb.tvAmountToSplit.bind(categorizeTransactionsVM.amountToCategorize) { text = it }
        // # Button
        vb.btnSave.setOnClickListener {
            if (!shouldIgnoreUserInput.value!!)
                categorizeTransactionsAdvancedVM.userSaveTransaction()
            nav.navigateUp()
        }
        // # TMTableView
        val categoryAmountRecipeFactory = ViewItemRecipeFactory3<ItemTextEditBinding, Map.Entry<Category, BigDecimal>>(
            { ItemTextEditBinding.inflate(LayoutInflater.from(context)) },
            { (category, amount), vb, _ ->
                vb.editText.setText(amount.toString())
                vb.editText.onDone {
                    if (!shouldIgnoreUserInput.value!!)
                        categorizeTransactionsAdvancedVM.userInputCA(category, it.toMoneyBigDecimal())
                }
                vb.editText.setOnCreateContextMenuListener { menu, _, _ ->
                    menu.add(
                        MenuItemPartial("Fill") {
                            _shouldIgnoreUserInputForDuration.onNext(Unit)
                            categorizeTransactionsAdvancedVM.userFillIntoCategory(category)
                        }
                    )
                }
            }
        )
        categorizeTransactionsAdvancedVM.transactionToPush
            .map { transaction ->
                val categoryAmounts = transaction.categoryAmounts.toSortedMap(categoryComparator)
                val recipes2D = listOf(
                    listOf(recipeFactories.header.createOne("Category"))
                            + recipeFactories.textView.createOne("Default")
                            + recipeFactories.textView.createMany(categoryAmounts.map { it.key.name }),
                    listOf(recipeFactories.header.createOne("Amount"))
                            + recipeFactories.textViewWithLifecycle.createOne(categorizeTransactionsAdvancedVM.defaultAmount)
                            + categoryAmountRecipeFactory.createMany(categoryAmounts.entries)
                ).reflectXY()
                val dividerMap = categoryAmounts.keys
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to recipeFactories.titledDivider.createOne(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, and default row
                Pair(recipes2D, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipes2D, dividerMap) ->
                vb.tmTableView.initialize(
                    recipeGrid = recipes2D,
                    shouldFitItemWidthsInsideTable = true,
                    dividerMap = dividerMap,
                    rowFreezeCount = 1,
                )
            }
    }
}