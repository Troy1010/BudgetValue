package com.tminus1010.budgetvalue.all_features.framework.view.recipe_factories

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.add
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.easyText2
import com.tminus1010.budgetvalue.all_features.framework.view.onDone
import com.tminus1010.tmcommonkotlin.misc.tmTableView.IViewItemRecipe3
import com.tminus1010.tmcommonkotlin.misc.tmTableView.ViewItemRecipe3
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.CategoryAmountFormulaVMItem
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.MenuVMItem
import com.tminus1010.budgetvalue.all_features.domain.Category
import com.tminus1010.budgetvalue.databinding.ItemAmountFormulaBinding
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import com.tminus1010.tmcommonkotlin.misc.extensions.bind
import com.tminus1010.tmcommonkotlin.tuple.Box
import com.tminus1010.tmcommonkotlin.view.extensions.easyVisibility
import io.reactivex.rxjava3.core.Observable

fun Fragment.itemAmountFormulaRF() = ItemAmountFormulaRecipeFactory(requireContext())

class ItemAmountFormulaRecipeFactory(private val context: Context) {
    val inflate: (LayoutInflater) -> ItemAmountFormulaBinding = ItemAmountFormulaBinding::inflate

    fun create(d: CategoryAmountFormulaVMItem, fillCategory: Observable<Box<Category?>>, requestRootView: () -> Unit, menuItems: Observable<List<MenuVMItem>>): IViewItemRecipe3 = ViewItemRecipe3(context, inflate) { vb ->
        vb.root.bind(fillCategory) { (it) -> isEnabled = d.category != it }
        vb.moneyEditText.onDone(d::userSetAmount)
        vb.moneyEditText.bind(menuItems) { setOnCreateContextMenuListener { menu, _, _ -> menu.add(it) } }
        vb.tvPercentage.bind(d.amountFormula) { easyVisibility = it is AmountFormula.Percentage }
        // requestRootView() is required for onDone to not accidentally capture the new text.
        vb.moneyEditText.bind(d.amountFormula) { requestRootView(); easyText2 = it.toDisplayStr() }
    }
}