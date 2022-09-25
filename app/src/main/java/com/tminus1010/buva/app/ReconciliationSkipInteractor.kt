package com.tminus1010.buva.app

import com.tminus1010.buva.data.ReconciliationSkipRepo
import com.tminus1010.buva.data.SettingsRepo
import com.tminus1010.buva.domain.ReconciliationSkipFactory
import com.tminus1010.buva.domain.TransactionBlock
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class ReconciliationSkipInteractor @Inject constructor(
    private val reconciliationSkipRepo: ReconciliationSkipRepo,
    private val reconciliationSkipFactory: ReconciliationSkipFactory,
    private val settingsRepo: SettingsRepo,
) {
    val reconciliationSkips = reconciliationSkipRepo.reconciliationSkips

    suspend fun push(localDate: LocalDate) {
        reconciliationSkipRepo.push(reconciliationSkipFactory.create(localDate))
    }

    suspend fun removeSkipIn(transactionBlock: TransactionBlock) {
        reconciliationSkips.first()
            .filter { transactionBlock.datePeriod != null && it.localDate(settingsRepo.anchorDateOffset.value) in transactionBlock.datePeriod }
            .also { reconciliationSkipRepo.delete(it) }
    }
}