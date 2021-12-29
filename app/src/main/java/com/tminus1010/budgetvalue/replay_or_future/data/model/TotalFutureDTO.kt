package com.tminus1010.budgetvalue.replay_or_future.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStatus

@Entity
data class TotalFutureDTO(
    @PrimaryKey
    val name: String,
    val searchTotal: String,
    val categoryAmountFormulasStr: String,
    val autoFillCategoryName: String,
    val terminationStatus: TerminationStatus,
)