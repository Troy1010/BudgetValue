package com.tminus1010.budgetvalue.replay.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BasicFutureDTO(
    @PrimaryKey
    val name: String,
    val searchText: String,
    val categoryAmountFormulasStr: String,
    val autoFillCategoryName: String,
)