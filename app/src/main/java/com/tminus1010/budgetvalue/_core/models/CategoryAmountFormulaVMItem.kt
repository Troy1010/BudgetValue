package com.tminus1010.budgetvalue._core.models

import com.tminus1010.budgetvalue._core.extensions.toMoneyBigDecimal
import com.tminus1010.budgetvalue._core.middleware.ColdObservable
import com.tminus1010.budgetvalue._core.middleware.ui.MenuVMItem
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.transactions.models.AmountFormula
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
