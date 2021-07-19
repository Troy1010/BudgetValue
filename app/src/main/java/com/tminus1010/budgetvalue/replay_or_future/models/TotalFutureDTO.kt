package com.tminus1010.budgetvalue.replay_or_future.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TotalFutureDTO(
    @PrimaryKey
    val name: String,
    val searchTotal: String,
    val categoryAmountFormulasStr: String,
    val autoFillCategoryName: String,
    val isPermanent: Boolean,
)