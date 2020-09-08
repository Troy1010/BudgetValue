package com.example.budgetvalue.layers.z_ui.misc

import com.example.budgetvalue.models.Category
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.math.BigDecimal

data class SplitRowData (
    val category: Category,
    val spent: BigDecimal,
    val income: BehaviorSubject<BigDecimal>
) {
    val budgeted = spent + income.value
}