package com.tminus1010.buva.ui.all_features.view_model_item

import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.framework.observable.ColdObservable
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.AmountFormula
import com.tminus1010.tmcommonkotlin.tuple.Box
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

data class CategoryAmountFormulaVMItem(
    val category: Category,
    private val _amountFormula: Observable<AmountFormula>,
    private val fillCategory: Observable<Box<Category?>>,
    private val fillAmountFormula: ColdObservable<AmountFormula>,
    private val userSetCategoryIsPercentage: (Category, Boolean) -> Unit,
    private val userInputCA: (Category, BigDecimal) -> Unit,
) {
    val isFillCategory =
        fillCategory
            .map { (it) -> it == category }
            .distinctUntilChanged()!!

    val amountFormula: Observable<AmountFormula> =
        isFillCategory
            .switchMap { if (it) fillAmountFormula else _amountFormula }

    fun userSetAmount(s: String) {
        userInputCA(category, s.toMoneyBigDecimal())
    }

    val menuVMItems: Observable<List<MenuVMItem>> =
        amountFormula
            .map {
                listOfNotNull(
                    if (it !is AmountFormula.Percentage)
                        MenuVMItem(
                            title = "Percentage",
                            onClick = { userSetCategoryIsPercentage(category, true) },
                        )
                    else null,
                    if (it !is AmountFormula.Value)
                        MenuVMItem(
                            title = "No Percentage",
                            onClick = { userSetCategoryIsPercentage(category, false) },
                        )
                    else null,
                )
            }
}
