package com.example.budgetvalue.layer_ui.misc

import com.example.budgetvalue.model_app.Category
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

data class SplitRowData (
    val category: Category,
    val spent: BigDecimal,
    val income: BehaviorSubject<BigDecimal>
) {
    val budgeted = spent + income.value
    fun getBudgeted2(incomeValue: BigDecimal): BigDecimal { // TODO: hacky
        return spent + incomeValue
    }
}