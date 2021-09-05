package com.tminus1010.budgetvalue.replay_or_future

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.extensions.*
import com.tminus1010.budgetvalue._core.middleware.ui.MenuVMItem
import com.tminus1010.budgetvalue._core.middleware.ui.onDone
import com.tminus1010.budgetvalue._core.middleware.ui.recipe_factories.*
import com.tminus1010.budgetvalue._core.middleware.ui.tmTableView3.bindItemCheckboxBinding
import com.tminus1010.budgetvalue._core.middleware.ui.viewBinding
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulaVMItem
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.databinding.FragCreateFutureBinding
import com.tminus1010.budgetvalue.databinding.ItemAmountFormulaBinding
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.budgetvalue.transactions.models.SearchType
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateFutureFrag : Fragment(R.layout.frag_create_future) {
    private val vb by viewBinding(FragCreateFutureBinding::bind)
    private val createFutureVM by navGraphViewModels<CreateFutureVM>(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }
    private val categorySelectionVM by navGraphViewModels<CategorySelectionVM>(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
        createFutureVM.setup(categorySelectionVM)
        createFutureVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        createFutureVM.navTo.observe(viewLifecycleOwner) { it(nav) }
        // # TMTableView OtherUserInput
        vb.tmTableViewOtherInput.bind(createFutureVM.searchType) { searchType ->
            initialize(
                listOfNotNull(
                    listOf(
                        itemTextViewRF().create(createFutureVM.totalGuessHeader),
                        itemMoneyEditTextRF().create(createFutureVM.totalGuess.map { it.toString() }, createFutureVM::userSetTotalGuess),
                    ),
                    listOf(
                        itemTextViewRF().create(createFutureVM.searchTypeHeader),
                        itemSpinnerRF().create(SearchType.values(), createFutureVM.searchType.value, createFutureVM::userSetSearchType),
                    ),
                    if (searchType == SearchType.DESCRIPTION_AND_TOTAL)
                        listOf(
                            itemTextViewRF().create(createFutureVM.searchDescriptionHeader),
                            itemEditTextRF().create(createFutureVM.searchDescription, createFutureVM::userSetSearchDescription, createFutureVM.searchDescriptionMenuVMItems),
                        )
                    else null,
                ),
                shouldFitItemWidthsInsideTable = true
            )
        }
        // # TMTableView CategoryAmounts
        createFutureVM.categoryAmountFormulaVMItems
            .map { categoryAmountFormulaVMItems ->
                listOf(
                    listOf(
                        itemHeaderRF().create(createFutureVM.categoryHeader),
                        itemHeaderRF().create(createFutureVM.amountHeader),
                        itemHeaderRF().create(createFutureVM.fillHeader),
                    ),
                    *categoryAmountFormulaVMItems.map {
                        listOf(
                            itemTextViewRF().create(it.category.name),
                            itemAmountFormulaRF().create(it, createFutureVM.fillCategory, { getView()?.requestFocus() }, it.menuVMItems),
                            itemCheckboxRF().create(it.isFillCategory, it.category.name, createFutureVM::userSetFillCategory),
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
            nav.navigate(R.id.createFutureNestedGraph)
        }
    }
}