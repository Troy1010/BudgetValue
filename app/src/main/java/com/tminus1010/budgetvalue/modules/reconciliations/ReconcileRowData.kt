package com.tminus1010.budgetvalue.modules.reconciliations

import com.tminus1010.budgetvalue.modules.categories.Category
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

data class ReconcileRowData(
    val category: Category,
    val plan: Observable<BigDecimal>,
    val actual: Observable<BigDecimal>,
    val reconcile: Observable<BigDecimal>
)