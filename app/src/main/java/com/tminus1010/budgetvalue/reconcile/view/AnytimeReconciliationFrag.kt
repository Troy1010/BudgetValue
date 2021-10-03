package com.tminus1010.budgetvalue.reconcile.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.middleware.Rx
import com.tminus1010.budgetvalue._core.middleware.view.recipe_factories.*
import com.tminus1010.budgetvalue._core.middleware.view.viewBinding
import com.tminus1010.budgetvalue.all.presentation_and_view._models.AccountVMItemList
import com.tminus1010.budgetvalue.reconcile.presentation.AnytimeReconciliationVM_OLD2
import com.tminus1010.budgetvalue.all.presentation_and_view.import_z.AccountsVM
import com.tminus1010.budgetvalue.budgeted.BudgetedVM
import com.tminus1010.budgetvalue.categories.CategoriesVM
import com.tminus1010.budgetvalue.databinding.FragReconcileBinding
import com.tminus1010.tmcommonkotlin.core.extensions.reflectXY
import com.tminus1010.tmcommonkotlin.misc.extensions.distinctUntilChangedWith
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class AnytimeReconciliationFrag : Fragment(R.layout.frag_reconcile) {
    private val vb by viewBinding(FragReconcileBinding::bind)
    private val anytimeReconciliationVMOLD2: AnytimeReconciliationVM_OLD2 by activityViewModels()
    private val categoriesVM: CategoriesVM by activityViewModels()
    private val accountsVM: AccountsVM by activityViewModels()
    private val budgetedVM: BudgetedVM by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // # Bind Incoming from Presentation layer
        // ## State
        vb.buttonsview.buttons = anytimeReconciliationVMOLD2.buttons
        // ## TMTableView
        Rx.combineLatest(categoriesVM.userCategories, anytimeReconciliationVMOLD2.activeReconcileCAsToShow, budgetedVM.categoryValidatedStringVMItems)
            .observeOn(Schedulers.computation())
            .debounce(100, TimeUnit.MILLISECONDS)
            .map { (categories, activeReconciliationCAs, budgetedCategoryValidatedStringVMItems) ->
                val recipeGrid = listOf(
                    listOf(itemHeaderRF().create("Category"))
                            + itemTextViewRB().create("Default")
                            + categories.map { itemTextViewRB().create(it.name) },
                    listOf(itemHeaderRF().create("Reconcile"))
                            + itemTextViewRB().create(anytimeReconciliationVMOLD2.defaultAmount)
                            + categories.map { category -> itemMoneyEditTextRF().create(activeReconciliationCAs[category]) { anytimeReconciliationVMOLD2.pushActiveReconcileCA(category, it) } },
                    listOf(itemHeaderWithSubtitleRF().create("Budgeted", accountsVM.accountVMItemList.map(AccountVMItemList::total)))
                            + itemTextViewRB().create(budgetedVM.defaultAmount)
                            + categories.map { itemTextViewRB().create(budgetedCategoryValidatedStringVMItems[it]) }
                ).reflectXY()
                val dividerMap = categories
                    .withIndex()
                    .distinctUntilChangedWith(compareBy { it.value.type })
                    .associate { it.index to itemTitledDividerRB().create(it.value.type.name) }
                    .mapKeys { it.key + 2 } // header row, default row
                Pair(recipeGrid, dividerMap)
            }
            .observe(viewLifecycleOwner) { (recipeGrid, dividerMap) ->
                vb.tmTableView.initialize(
                    recipeGrid = recipeGrid,
                    shouldFitItemWidthsInsideTable = true,
                    dividerMap = dividerMap,
                    rowFreezeCount = 1,
                )
            }
    }

    companion object {
        fun navTo(nav: NavController) {
            nav.navigate(R.id.reconcileFrag)
        }
    }
}
