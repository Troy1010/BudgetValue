package com.tminus1010.budgetvalue.replay_or_future

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.navGraphViewModels
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.*
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.databinding.FragCreateFutureBinding
import com.tminus1010.budgetvalue.transactions.models.SearchType
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.remove
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateFutureFrag : Fragment(R.layout.frag_create_future) {
    private val vb by viewBinding(FragCreateFutureBinding::bind)
    private val createFutureVM by navGraphViewModels<CreateFutureVM>(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }
    private val categorySelectionVM by navGraphViewModels<CategorySelectionVM>(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
        createFutureVM.setup(categorySelectionVM) { nav.getBackStackEntry(R.id.categorizeNestedGraph).viewModelStore.remove<CreateFutureVM>() }
        createFutureVM.navUp.observe(viewLifecycleOwner) { nav.navigateUp() }
        createFutureVM.navTo.observe(viewLifecycleOwner) { it(nav) }
        // # TMTableView OtherUserInput
        vb.tmTableViewOtherInput.bind(createFutureVM.searchType) { searchType ->
            initialize(
                listOfNotNull(
                    listOf(
                        itemTextViewRB().create(createFutureVM.totalGuessHeader),
                        itemMoneyEditTextRF().create(createFutureVM.totalGuess.map { it.toString() }, createFutureVM::userSetTotalGuess),
                    ),
                    listOf(
                        itemTextViewRB().create(createFutureVM.searchTypeHeader),
                        itemSpinnerRF().create(SearchType.values(), createFutureVM.searchType.value, createFutureVM::userSetSearchType),
                    ),
                    if (listOf(SearchType.DESCRIPTION_AND_TOTAL, SearchType.DESCRIPTION).any { it == searchType })
                        listOf(
                            itemTextViewRB().create(createFutureVM.searchDescriptionHeader),
                            itemEditTextRF().create(createFutureVM.searchDescription, createFutureVM::userSetSearchDescription, createFutureVM.searchDescriptionMenuVMItems),
                        )
                    else null,
                    listOf(
                        itemTextViewRB().create(createFutureVM.isPermanentHeader),
                        itemCheckboxRF().create(createFutureVM.isPermanent.value, createFutureVM::userSetIsPermanent),
                    ),
                ),
                shouldFitItemWidthsInsideTable = true
            )
        }
        // # TMTableView CategoryAmounts
        createFutureVM.categoryAmountFormulaVMItems
            .map { categoryAmountFormulaVMItems ->
                val recipeGrid = listOf(
                    listOf(
                        itemHeaderRF().create(createFutureVM.categoryHeader),
                        itemHeaderRF().create(createFutureVM.amountHeader),
                        itemHeaderRF().create(createFutureVM.fillHeader),
                    ),
                    *categoryAmountFormulaVMItems.map {
                        listOf(
                            itemTextViewRB().create(it.category.name),
                            itemAmountFormulaRF().create(it, createFutureVM.fillCategory, { getView()?.requestFocus() }, it.menuVMItems),
                            itemCheckboxRF().create(it.isFillCategory, it.category.name, createFutureVM::userSetFillCategory),
                        )
                    }.toTypedArray(),
                )
                val dividerMap = categoryAmountFormulaVMItems.map { it.category }
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to itemTitledDividerRB().create(it.value.type.name) }
                    .mapKeys { it.key + 1 } // header row
                Pair(recipeGrid, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipeGrid, dividerMap) ->
                vb.tmTableViewCategoryAmounts.initialize(
                    recipeGrid,
                    shouldFitItemWidthsInsideTable = true,
                    dividerMap = dividerMap
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