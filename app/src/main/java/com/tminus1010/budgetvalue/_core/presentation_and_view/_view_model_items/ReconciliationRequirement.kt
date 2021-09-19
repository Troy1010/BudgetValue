package com.tminus1010.budgetvalue._core.presentation_and_view._view_model_items

import com.tminus1010.budgetvalue.all.domain.TransactionBlock
import com.tminus1010.budgetvalue.plans.models.Plan

/**
 * This represents a reason why a Reconciliation is required
 */
sealed class ReconciliationRequirement {
    class PlanNeedsReconcile(val plan: Plan, val transactionBlock: TransactionBlock) : ReconciliationRequirement()
    object AccountsTotalNeedsReconcile : ReconciliationRequirement()
}