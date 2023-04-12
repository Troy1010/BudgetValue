package com.tminus1010.buva.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

sealed class ResolutionStrategy : Parcelable {
    @Parcelize
    object MatchPlan : ResolutionStrategy() {
        /**
         * Calculate a ActiveReconciliationValue which would resolve an invalid BudgetValue.
         * (Assumes what an invalid BudgetValue would be based on class input)
         */
        fun calc(category: Category, activeReconciliationCAs: CategoryAmounts, budgetedCAs: CategoryAmounts, activePlanCAs: CategoryAmounts): BigDecimal {
            val budgetedValueIfNoActiveReconciliation = (budgetedCAs[category] ?: BigDecimal.ZERO) - (activeReconciliationCAs[category] ?: BigDecimal.ZERO)
            val activePlanValue = activePlanCAs[category] ?: BigDecimal.ZERO
            // Always match the activePlanValue.
            return activePlanValue - budgetedValueIfNoActiveReconciliation
        }
    }

    @Parcelize
    data class Basic(val budgetedMin: BigDecimal?) : ResolutionStrategy() {
        constructor(budgetedMin: Int) : this(budgetedMin.toBigDecimal())

        /**
         * Calculate a ActiveReconciliationValue which would resolve an invalid BudgetValue.
         * (Assumes what an invalid BudgetValue would be based on class input)
         */
        fun calc(category: Category, activeReconciliationCAs: CategoryAmounts, budgetedCAs: CategoryAmounts): BigDecimal {
            val budgetedValueIfNoActiveReconciliation = (budgetedCAs[category] ?: BigDecimal.ZERO) - (activeReconciliationCAs[category] ?: BigDecimal.ZERO)
            // If the current value is good, then leave it as-is.
            return if (budgetedMin == null || budgetedValueIfNoActiveReconciliation >= budgetedMin)
                activeReconciliationCAs[category] ?: BigDecimal.ZERO
            else
                budgetedMin - budgetedValueIfNoActiveReconciliation
        }
    }
}