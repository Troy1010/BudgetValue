package com.tminus1010.budgetvalue.replay_or_future

import android.os.Bundle
import android.view.View
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
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateFutureFrag : Fragment(R.layout.frag_create_future) {
    private val vb by viewBinding(FragCreateFutureBinding::bind)
    private val createFutureVM by viewModels<CreateFutureVM>()
    private val categorySelectionVM by navGraphViewModels<CategorySelectionVM>(R.id.categorizeNestedGraph) { defaultViewModelProviderFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # TMTableView CategoryAmounts
        val bindItemHeaderBinding = { d: String, vb: ItemHeaderBinding, _: LifecycleOwner ->
            vb.textview.text = d
        }
        val bindItemTextViewBinding = { d: Category, vb: ItemTextViewBinding, _: LifecycleOwner ->
            vb.textview.text = d.name
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
        createFutureVM.setup(categorySelectionVM)
        createFutureVM.categoryAmountFormulaVMItems
            .map { categoryAmountFormulaVMItems ->
                listOf(
                    listOf(
                        viewItemRecipe(bindItemHeaderBinding, "Category"),
                        viewItemRecipe(bindItemHeaderBinding, "Amount"),
                        viewItemRecipe(bindItemHeaderBinding, "Fill"),
                    ),
                    *categoryAmountFormulaVMItems.map {
                        listOf(
                            viewItemRecipe(bindItemTextViewBinding, it.category),
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
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.createFutureFrag)
        }
    }
}