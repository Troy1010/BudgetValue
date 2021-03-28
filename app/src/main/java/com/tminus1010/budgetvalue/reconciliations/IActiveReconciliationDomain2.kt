package com.tminus1010.budgetvalue.reconciliations

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal

interface IActiveReconciliationDomain2 {
    // This calculation is a bit confusing. Take a look at ManualCalculationsForTests for clarification
    val defaultAmount: Observable<BigDecimal>
    val intentSaveReconciliation: PublishSubject<Unit>
}