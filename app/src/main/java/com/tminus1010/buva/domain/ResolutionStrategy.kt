package com.tminus1010.buva.domain

import android.os.Parcelable
import com.tminus1010.buva.all_layers.extensions.isNegative
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

sealed class ResolutionStrategy : Parcelable {
    @Parcelize
    object MatchPlan : ResolutionStrategy() {
        /**
         * Calculate a ActiveReconciliationValue which would resolve an invalid BudgetValue.
         * (Assumes what an invalid BudgetValue would be based on class input)
         */
        fun calc(category: Category, budgetedAmount: BigDecimal?, activeReconciliationCAs: CategoryAmounts, activePlanCAs: CategoryAmounts): BigDecimal {
            val budgetedValueIfNoActiveReconciliation = (budgetedAmount ?: BigDecimal.ZERO) - (activeReconciliationCAs[category] ?: BigDecimal.ZERO)
            val activePlanValue = activePlanCAs[category] ?: BigDecimal.ZERO
            // Always match the activePlanValue.
            return activePlanValue - budgetedValueIfNoActiveReconciliation
        }

        fun validation(category: Category, budgetedAmount: BigDecimal?, activeReconciliationCAs: CategoryAmounts, activePlanCAs: CategoryAmounts): Validation {
            return if (activeReconciliationCAs[category] == calc(category, budgetedAmount, activeReconciliationCAs, activePlanCAs)) Validation.Success else Validation.Failure
        }
    }

    @Parcelize
    data class Basic(val budgetedMin: BigDecimal = BigDecimal.ZERO) : ResolutionStrategy() {
        constructor(budgetedMin: Int) : this(budgetedMin.toBigDecimal())

        init {
            if (budgetedMin.isNegative) error("Negative budgetedMin is not supported.")
        }

        /**
         * Calculate a ActiveReconciliationValue which would resolve an invalid BudgetValue.
         * (Assumes what an invalid BudgetValue would be based on class input)
         */
        fun calc(category: Category, budgetedAmount: BigDecimal?, activeReconciliationCAs: CategoryAmounts): BigDecimal {
            val budgetedValueIfNoActiveReconciliation = (budgetedAmount ?: BigDecimal.ZERO) - (activeReconciliationCAs[category] ?: BigDecimal.ZERO)
            // If the current value is good, then leave it as-is.
            return if (budgetedValueIfNoActiveReconciliation >= budgetedMin)
                activeReconciliationCAs[category] ?: BigDecimal.ZERO
            else
                budgetedMin - budgetedValueIfNoActiveReconciliation
        }

        fun validation(category: Category, amount: BigDecimal?): Validation {
            return if ((amount ?: BigDecimal.ZERO) >= budgetedMin) Validation.Success else Validation.Failure
        }
    }
}