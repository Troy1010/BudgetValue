package com.tminus1010.buva.app

import com.tminus1010.buva.data.ReconciliationSkipRepo
import com.tminus1010.buva.domain.ReconciliationSkipFactory
import java.time.LocalDate
import javax.inject.Inject

class ReconciliationSkipInteractor @Inject constructor(
    private val reconciliationSkipRepo: ReconciliationSkipRepo,
    private val reconciliationSkipFactory: ReconciliationSkipFactory,
) {
    val reconciliationSkips = reconciliationSkipRepo.reconciliationSkips

    suspend fun push(localDate: LocalDate) {
        reconciliationSkipRepo.push(reconciliationSkipFactory.create(localDate))
    }
}