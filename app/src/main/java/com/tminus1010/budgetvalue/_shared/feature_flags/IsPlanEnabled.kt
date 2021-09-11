package com.tminus1010.budgetvalue._shared.feature_flags

import com.tminus1010.budgetvalue._core.extensions.isZero
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import com.tminus1010.tmcommonkotlin.rx.extensions.pairwise
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class IsPlanEnabled @Inject constructor(
    private val transactionsDomain: TransactionsDomain,
) {
    operator fun invoke(): Observable<Boolean> {
        return transactionsDomain.transactionBlocks
            .map {
                it
                    .filter { it.defaultAmount.isZero }
                    .size > 2
            }
            .distinctUntilChanged()
    }

    val onChangeToTrue = invoke()
        .pairwise()
        .filter { it.second }
        .map { Unit }
}