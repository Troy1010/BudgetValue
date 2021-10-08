package com.tminus1010.budgetvalue.reconcile.app.convenience_service

import com.tminus1010.budgetvalue.reconcile.app.interactor.ActiveReconciliationDefaultAmountInteractor
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import java.math.BigDecimal
import javax.inject.Inject

class ActiveReconciliationDefaultAmount @Inject constructor(
    private val activeReconciliationDefaultAmountInteractor: ActiveReconciliationDefaultAmountInteractor
) : Observable<BigDecimal>() {
    override fun subscribeActual(observer: Observer<in BigDecimal>?) {
        activeReconciliationDefaultAmountInteractor.activeReconciliationDefaultAmount.subscribe(observer)
    }
}