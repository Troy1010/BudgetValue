package com.tminus1010.budgetvalue.replay_or_future

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.*
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItem
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulaVMItem
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.*
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.SearchType
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.atomic.AtomicBoolean


@AndroidEntryPoint
class CreateFutureFrag : Fragment(R.layout.frag_create_future) {
    private val vb by viewBinding(FragCreateFutureBinding::bind)
    private val createFutureVM by viewModels<CreateFutureVM>()
    private val categorySelectionVM by navGraphViewModels<CategorySelectionVM>(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
        createFutureVM.setup(categorySelectionVM)
        createFutureVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        // # bind methods
        val bindItemHeaderBinding = { d: String, vb: ItemHeaderBinding, _: LifecycleOwner ->
            vb.textview.text = d
        }
        val bindItemTextViewBinding = { d: String, vb: ItemTextViewBinding, _: LifecycleOwner ->
            vb.textview.text = d
        }
        val bindItemEditTextBinding = { d: Observable<String>, vb: ItemEditTextBinding, lifecycle: LifecycleOwner ->
            vb.edittext.lifecycle = lifecycle
            vb.edittext.bind(d) { easyText = it }
        }
        val bindItemSpinnerBinding = { _: Unit, vb: ItemSpinnerBinding, _: LifecycleOwner ->
            val adapter = ArrayAdapter(requireContext(), R.layout.item_text_view_without_highlight, SearchType.values())
            vb.spinner.adapter = adapter
            vb.spinner.setSelection(adapter.getPosition(createFutureVM.searchType.value!!))
            vb.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                var didFirstSelectionHappen = AtomicBoolean(false)
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    if (didFirstSelectionHappen.getAndSet(true))
                        createFutureVM.userSetSearchType((vb.spinner.selectedItem as SearchType))
                }

                override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            }
        }
        val bindItemMoneyEditTextBinding = { d: String, vb: ItemMoneyEditTextBinding, _: LifecycleOwner ->
            vb.moneyedittext.easyText = d
            vb.moneyedittext.onDone { createFutureVM.userSetTotalGuess(it) }
        }
        val bindItemMoneyEditTextBinding2 = { d: Observable<String>, vb: ItemMoneyEditTextBinding, lifecycle: LifecycleOwner ->
            vb.moneyedittext.lifecycle = lifecycle
            vb.moneyedittext.bind(d) { easyText = it }
            vb.moneyedittext.onDone { createFutureVM.userSetTotalGuess(it) }
        }
        val bindItemAmountFormulaBinding = { d: CategoryAmountFormulaVMItem, vb: ItemAmountFormulaBinding, lifecycle: LifecycleOwner ->
            val category = d.category
            val amountFormula = d.amountFormula
            vb.moneyEditText.bind(createFutureVM.fillCategory, lifecycle) {
                isEnabled = category != it
                setBackgroundColor(context.theme.getColorByAttr(if (isEnabled) R.attr.colorBackground else R.attr.colorBackgroundHighlight))
            }
            vb.moneyEditText.onDone { createFutureVM.userInputCA(category, it.toMoneyBigDecimal()) }
            amountFormula.observe(lifecycle) { _amountFormula ->
                vb.tvPercentage.easyVisibility = _amountFormula is AmountFormula.Percentage
                getView()?.requestFocus() // required for onDone to not accidentally capture the new text.
                vb.moneyEditText.setText(_amountFormula.toDisplayStr())
                vb.moneyEditText.setOnCreateContextMenuListener { menu, _, _ ->
                    menu.add(
                        *listOfNotNull(
                            if (_amountFormula !is AmountFormula.Percentage)
                                MenuItem(
                                    title = "Percentage",
                                    onClick = { createFutureVM.userSwitchCategoryIsPercentage(category, true) })
                            else null,
                            if (_amountFormula !is AmountFormula.Value)
                                MenuItem(
                                    title = "No Percentage",
                                    onClick = { createFutureVM.userSwitchCategoryIsPercentage(category, false) })
                            else null,
                        ).toTypedArray()
                    )
                }
            }
            Unit
        }
        val bindItemCheckboxBinding = { d: Category, vb: ItemCheckboxBinding, lifecycle: LifecycleOwner ->
            vb.checkbox.bind(createFutureVM.fillCategory, lifecycle) {
                isChecked = d == it
                isEnabled = d != it
            }
            vb.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) createFutureVM.userSetFillCategory(d)
            }
        }
        // # TMTableView OtherUserInput
        vb.tmTableViewOtherInput.initialize(
            listOf(
                listOf(
                    viewItemRecipe(bindItemTextViewBinding, createFutureVM.totalGuessHeader),
                    viewItemRecipe(bindItemMoneyEditTextBinding2, createFutureVM.totalGuess.map { it.toString() }),
                ),
                listOf(
                    viewItemRecipe(bindItemTextViewBinding, createFutureVM.searchTypeHeader),
                    viewItemRecipe(bindItemSpinnerBinding),
                ),
                listOf(
                    viewItemRecipe(bindItemTextViewBinding, createFutureVM.searchDescriptionHeader),
                    viewItemRecipe(bindItemEditTextBinding, createFutureVM.searchDescription),
                ),
            ),
            shouldFitItemWidthsInsideTable = true
        )
        // # TMTableView CategoryAmounts
        createFutureVM.categoryAmountFormulaVMItems
            .map { categoryAmountFormulaVMItems ->
                listOf(
                    listOf(
                        viewItemRecipe(bindItemHeaderBinding, createFutureVM.categoryHeader),
                        viewItemRecipe(bindItemHeaderBinding, createFutureVM.amountHeader),
                        viewItemRecipe(bindItemHeaderBinding, createFutureVM.fillHeader),
                    ),
                    *categoryAmountFormulaVMItems.map {
                        listOf(
                            viewItemRecipe(bindItemTextViewBinding, it.category.name),
                            viewItemRecipe(bindItemAmountFormulaBinding, it),
                            viewItemRecipe(bindItemCheckboxBinding, it.category),
                        )
                    }.toTypedArray(),
                )
            }
            .observe(viewLifecycleOwner) { recipeGrid ->
                vb.tmTableViewCategoryAmounts.initialize(
                    recipeGrid,
                    shouldFitItemWidthsInsideTable = true
                )
            }
        // # ButtonsView
        vb.buttonsview.buttons = createFutureVM.buttonVMItems
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.createFutureFrag)
        }
    }
}