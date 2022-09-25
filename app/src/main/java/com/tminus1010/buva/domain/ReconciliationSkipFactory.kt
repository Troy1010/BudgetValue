package com.tminus1010.buva.domain

import com.tminus1010.buva.data.SettingsRepo
import java.time.LocalDate
import javax.inject.Inject

data class ReconciliationSkipFactory @Inject constructor(
    private val settingsRepo: SettingsRepo,
) {
    fun create(localDate: LocalDate): ReconciliationSkip {
        return ReconciliationSkip(localDate.minusDays(settingsRepo.anchorDateOffset.value))
    }
}