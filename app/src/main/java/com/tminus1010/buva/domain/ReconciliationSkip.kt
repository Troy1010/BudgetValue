package com.tminus1010.buva.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class ReconciliationSkip internal constructor(
    @PrimaryKey
    val unadjustedlocalDate: LocalDate,
) {
    fun localDate(anchorDateOffset: Long): LocalDate {
        return unadjustedlocalDate.plusDays(anchorDateOffset)
    }
}