package com.tminus1010.budgetvalue.all.app.interactors

import com.tminus1010.budgetvalue.all.domain.models.ReconciliationToDo
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

// TODO()
class ReconciliationsToDoInteractor @Inject constructor(
) {
    val reconciliationsToDo =
        Observable.just(
            listOf(
                ReconciliationToDo.Accounts
            )
        )!!
}