package com.tminus1010.budgetvalue.reconcile.app

import com.tminus1010.budgetvalue.transactions.app.TransactionBlock
import com.tminus1010.budgetvalue.plans.models.Plan
import java.math.BigDecimal

// TODO("Anytime is not exactly something ToDo.. perhaps this should be renamed?")
sealed class ReconciliationToDo {
    class PlanZ(val plan: Plan, val transactionBlock: TransactionBlock) : ReconciliationToDo()
    class Accounts(val difference: BigDecimal) : ReconciliationToDo()
    object Anytime : ReconciliationToDo()
}