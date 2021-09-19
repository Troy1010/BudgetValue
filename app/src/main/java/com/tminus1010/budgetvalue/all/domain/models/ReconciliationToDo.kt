package com.tminus1010.budgetvalue.all.domain.models

import com.tminus1010.budgetvalue.plans.models.Plan

sealed class ReconciliationToDo {
    class PlanZ(val plan: Plan, val transactionBlock: TransactionBlock)
    object Accounts
}