package com.tminus1010.budgetvalue.domain

import com.tminus1010.budgetvalue._unrestructured.transactions.app.TransactionBlock
import java.math.BigDecimal

// TODO("Anytime is not exactly something ToDo.. perhaps this should be renamed?")
sealed class ReconciliationToDo {
    class PlanZ(val plan: Plan, val transactionBlock: TransactionBlock) : ReconciliationToDo()
    class Accounts(val difference: BigDecimal) : ReconciliationToDo()
    object Anytime : ReconciliationToDo()
}