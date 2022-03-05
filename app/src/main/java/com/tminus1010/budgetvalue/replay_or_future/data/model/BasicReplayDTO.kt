package com.tminus1010.budgetvalue.replay_or_future.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BasicReplayDTO(
    @PrimaryKey
    val name: String,
    val searchTextsStr: String, // TODO: Just use List<String>, b/c adapter can handle it now.
    val categoryAmountFormulasStr: String,
    val autoFillCategoryName: String,
)