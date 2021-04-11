package com.tminus1010.budgetvalue.reconciliations.domain

import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

interface IActiveReconciliationDomain2 {
    val defaultAmount: Observable<BigDecimal>
}