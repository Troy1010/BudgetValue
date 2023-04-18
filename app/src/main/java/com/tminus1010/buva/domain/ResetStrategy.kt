package com.tminus1010.buva.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

sealed class ResetStrategy : Parcelable {
    /**
     * Whenever a reset occurs, [budgetedMax] determines how the reconciliation value will adjust so that Budgeted is at most [budgetedMax]
     */
    @Parcelize
    data class Basic(val budgetedMax: BigDecimal = BigDecimal.ZERO) : ResetStrategy() {
        constructor(budgetedMax: Int) : this(budgetedMax.toBigDecimal())

        fun calc(category: Category, activeReconciliationCAs: CategoryAmounts, budgetedCAs: CategoryAmounts): BigDecimal {
            val budgetedValueIfNoActiveReconciliation = (budgetedCAs[category] ?: BigDecimal.ZERO) - (activeReconciliationCAs[category] ?: BigDecimal.ZERO)
            return if (budgetedValueIfNoActiveReconciliation < budgetedMax)
                BigDecimal.ZERO
            else
                budgetedMax - budgetedValueIfNoActiveReconciliation
        }
    }
}