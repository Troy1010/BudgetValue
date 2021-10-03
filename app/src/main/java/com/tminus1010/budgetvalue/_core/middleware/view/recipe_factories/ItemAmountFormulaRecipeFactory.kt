package com.tminus1010.budgetvalue._core.middleware.view.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue._core.all.extensions.add
import com.tminus1010.budgetvalue._core.all.extensions.bind
import com.tminus1010.budgetvalue._core.all.extensions.easyText
import com.tminus1010.budgetvalue._core.all.extensions.easyVisibility
import com.tminus1010.budgetvalue._core.middleware.presentation.MenuVMItem
import com.tminus1010.budgetvalue._core.middleware.view.onDone
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.IViewItemRecipe3
import com.tminus1010.budgetvalue._core.middleware.view.tmTableView3.ViewItemRecipe3__
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulaVMItem
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.databinding.ItemAmountFormulaBinding
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable

fun Fragment.itemAmountFormulaRF() = ItemAmountFormulaRecipeFactory(requireContext())

class ItemAmountFormulaRecipeFactory(private val context: Context) {
    val inflate: (LayoutInflater) -> ItemAmountFormulaBinding = ItemAmountFormulaBinding::inflate

    fun create(d: CategoryAmountFormulaVMItem, fillCategory: Observable<Box<Category?>>, requestRootView: () -> Unit, menuItems: Observable<List<MenuVMItem>>): IViewItemRecipe3 = ViewItemRecipe3__(context, inflate) { vb ->
        vb.root.bind(fillCategory) { (it) -> isEnabled = d.category != it }
        vb.moneyEditText.onDone(d::userSetAmount)
        vb.moneyEditText.bind(menuItems) { setOnCreateContextMenuListener { menu, _, _ -> menu.add(it) } }
        vb.tvPercentage.bind(d.amountFormula) { easyVisibility = it is AmountFormula.Percentage }
        // requestRootView() is required for onDone to not accidentally capture the new text.
        vb.moneyEditText.bind(d.amountFormula) { requestRootView(); easyText = it.toDisplayStr() }
    }
}