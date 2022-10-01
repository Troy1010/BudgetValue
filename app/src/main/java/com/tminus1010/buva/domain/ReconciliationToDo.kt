package com.tminus1010.buva.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

sealed class ReconciliationToDo {
    @Parcelize
    class PlanZ(val transactionBlock: TransactionBlock) : ReconciliationToDo(), Parcelable
    class Accounts(val date: LocalDate) : ReconciliationToDo()
    object Anytime : ReconciliationToDo()
}