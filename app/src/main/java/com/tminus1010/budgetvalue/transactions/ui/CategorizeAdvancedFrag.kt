package com.tminus1010.budgetvalue.transactions.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.thekhaeng.recyclerviewmargin.LayoutMarginDecoration
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.categoryComparator
import com.tminus1010.budgetvalue._core.extensions.add
import com.tminus1010.budgetvalue._core.extensions.bind
import com.tminus1010.budgetvalue._core.extensions.easyText
import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ui.*
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.ViewItemRecipeFactory3
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.recipeFactories
import com.tminus1010.budgetvalue._core.ui.data_binding.bindButtonRVItem
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.FragCategorizeAdvancedBinding
import com.tminus1010.budgetvalue.databinding.ItemButtonBinding
import com.tminus1010.budgetvalue.databinding.ItemEditTextBinding
import com.tminus1010.budgetvalue.databinding.ItemMoneyEditTextBinding
import com.tminus1010.budgetvalue.transactions.CategorizeAdvancedVM
import com.tminus1010.budgetvalue.transactions.CategorizeVM
import com.tminus1010.budgetvalue.transactions.domain.CategorizeAdvancedDomain
import com.tminus1010.budgetvalue.transactions.domain.SaveTransactionDomain
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toPX
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class CategorizeAdvancedFrag : Fragment(R.layout.frag_categorize_advanced) {
    private val vb by viewBinding(FragCategorizeAdvancedBinding::bind)

    @Inject
    lateinit var categorizeTransactionsAdvancedDomain: CategorizeAdvancedDomain

    @Inject
    lateinit var saveTransactionDomain: SaveTransactionDomain
    private val categorizeVM: CategorizeVM by activityViewModels()
    private val categorizeAdvancedVM: CategorizeAdvancedVM by activityViewModels()
    private var _shouldIgnoreUserInputForDuration = PublishSubject.create<Unit>()
    private var shouldIgnoreUserInput = _shouldIgnoreUserInputForDuration
        .flatMap { Observable.just(false).delay(1, TimeUnit.SECONDS).startWithItem(true) }
        .startWithItem(false)
        .replay(1).autoConnect()
    var btns = emptyList<ButtonRVItem>()
        set(value) {
            field = value; vb.recyclerviewButtons.adapter?.notifyDataSetChanged()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shouldIgnoreUserInput.observe(viewLifecycleOwner) {}
        vb.tvAmountToSplit.bind(categorizeVM.amountToCategorize) { text = it }
        // # TMTableView
        val categoryAmountRecipeFactory = ViewItemRecipeFactory3<ItemMoneyEditTextBinding, Map.Entry<Category, BigDecimal>>(
            { ItemMoneyEditTextBinding.inflate(LayoutInflater.from(context)) },
            { (category, amount), vb, _ ->
                vb.editText.setText(amount.toString())
                vb.editText.onDone {
                    if (!shouldIgnoreUserInput.value!!)
                        categorizeAdvancedVM.userInputCA(category, it.toMoneyBigDecimal())
                }
                vb.editText.setOnCreateContextMenuListener { menu, _, _ ->
                    menu.add(
                        MenuItemPartial("Fill") {
                            _shouldIgnoreUserInputForDuration.onNext(Unit)
                            categorizeAdvancedVM.userFillIntoCategory(category)
                        }
                    )
                }
            }
        )
        categorizeAdvancedVM.transactionToPush
            .map { transaction ->
                val categoryAmounts = transaction.categoryAmounts.toSortedMap(categoryComparator)
                val recipes2D = listOf(
                    listOf(recipeFactories.header.createOne("Category"))
                            + recipeFactories.textView.createOne("Default")
                            + recipeFactories.textView.createMany(categoryAmounts.map { it.key.name }),
                    listOf(recipeFactories.header.createOne("Amount"))
                            + recipeFactories.textViewWithLifecycle.createOne(categorizeAdvancedVM.defaultAmount)
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

        // # Button RecyclerView
        vb.recyclerviewButtons.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        vb.recyclerviewButtons.addItemDecoration(LayoutMarginDecoration(8.toPX(requireContext())))
        vb.recyclerviewButtons.adapter = object : LifecycleRVAdapter<GenViewHolder2<ItemButtonBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                ItemButtonBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
                    .let { GenViewHolder2(it) }

            override fun onViewAttachedToWindow(holder: GenViewHolder2<ItemButtonBinding>, lifecycle: LifecycleOwner) {
                holder.vb.btnItem.bindButtonRVItem(lifecycle, btns[holder.adapterPosition])
            }

            override fun getItemCount() = btns.size
        }
        btns = listOfNotNull(
            ButtonRVItem(
                title = "Setup Auto Replay",
                onClick = {
                    categorizeAdvancedVM.userBeginAutoReplay()
                    nav.navigateUp()
                }
            ),
            ButtonRVItem(
                title = "Save Replay",
                onClick = {
                    val editText = EditText(requireContext())
                    AlertDialog.Builder(requireContext())
                        .setMessage("What would you like to name this replay?")
                        .setView(editText)
                        .setPositiveButton("Yes") { _, _ ->
                            categorizeTransactionsAdvancedVM.userSaveReplay(editText.easyText)
                            nav.navigateUp()
                        }
                        .setNegativeButton("No") { _, _ -> }
                        .show()
                }
            ),
            ButtonRVItem(
                title = "Save",
                onClick = {
                    categorizeAdvancedVM.userSaveTransaction()
                    nav.navigateUp()
                }
            ),
        ).reversed()
    }
}