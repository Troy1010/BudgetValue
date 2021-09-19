package com.tminus1010.budgetvalue.all.domain.models

import com.tminus1010.budgetvalue.plans.models.Plan

// TODO("Anytime is not exactly something ToDo.. perhaps this should be renamed?")
sealed class ReconciliationToDo {
    class PlanZ(val plan: Plan, val transactionBlock: TransactionBlock) : ReconciliationToDo()
    object Accounts : ReconciliationToDo()
    object Anytime : ReconciliationToDo()
}