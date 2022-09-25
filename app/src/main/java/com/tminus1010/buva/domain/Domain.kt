package com.tminus1010.buva.domain

object Domain {
    fun shouldSkip(reconciliationSkips: List<ReconciliationSkip>, transactionBlock: TransactionBlock, anchorDateOffset: Long): Boolean {
        if (transactionBlock.datePeriod == null) return false
        return reconciliationSkips.any { it.localDate(anchorDateOffset) in transactionBlock.datePeriod }
    }
}