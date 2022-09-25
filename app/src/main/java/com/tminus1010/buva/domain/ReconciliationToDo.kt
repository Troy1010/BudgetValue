package com.tminus1010.buva.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

// TODO("Anytime is not exactly something ToDo.. perhaps this should be renamed?")
sealed class ReconciliationToDo {
    @Parcelize
    class PlanZ(val plan: Plan, val transactionBlock: TransactionBlock) : ReconciliationToDo(), Parcelable
    class Accounts(val difference: BigDecimal) : ReconciliationToDo()
    object Anytime : ReconciliationToDo()
}