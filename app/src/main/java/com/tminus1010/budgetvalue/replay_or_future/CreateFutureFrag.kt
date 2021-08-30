package com.tminus1010.budgetvalue.replay_or_future

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.*
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItem
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.*
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulaVMItem
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.databinding.FragCreateFutureBinding
import com.tminus1010.budgetvalue.databinding.ItemAmountFormulaBinding
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.SearchType
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint


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
        val bindItemAmountFormulaBinding = { d: CategoryAmountFormulaVMItem, vb: ItemAmountFormulaBinding ->
            val category = d.category
            val amountFormula = d.amountFormula
            vb.moneyEditText.bind(createFutureVM.fillCategory) {
                isEnabled = category != it
                setBackgroundColor(context.theme.getColorByAttr(if (isEnabled) R.attr.colorBackground else R.attr.colorBackgroundHighlight))
            }
            vb.moneyEditText.onDone { createFutureVM.userInputCA(category, it.toMoneyBigDecimal()) }
            amountFormula.observe(vb.root.lifecycleOwner!!) { _amountFormula ->
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
        // # TMTableView OtherUserInput
        vb.tmTableViewOtherInput.initialize(
            listOf(
                listOf(
                    viewItemRecipe(bindItemTextViewBinding, createFutureVM.totalGuessHeader),
                    viewItemRecipe(bindItemMoneyEditTextBinding2(createFutureVM::userSetTotalGuess), createFutureVM.totalGuess.map { it.toString() }),
                ),
                listOf(
                    viewItemRecipe(bindItemTextViewBinding, createFutureVM.searchTypeHeader),
                    viewItemRecipe(bindItemSpinnerBinding(SearchType.values(), createFutureVM::userSetSearchType), createFutureVM.searchType.value!!),
                ),
                listOf(
                    viewItemRecipe(bindItemTextViewBinding, createFutureVM.searchDescriptionHeader),
                    viewItemRecipe(bindItemEditTextBinding(createFutureVM::userSetSearchDescription), createFutureVM.searchDescription),
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
                            viewItemRecipe(bindItemCheckboxBinding(it.category.name, createFutureVM::userSetFillCategory), createFutureVM.fillCategory.map { fillCategory -> fillCategory == it.category }),
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