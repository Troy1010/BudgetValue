package com.tminus1010.budgetvalue.replay.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.categories.models.Category

@Entity
data class BasicReplayDTO(
    @PrimaryKey
    val name: String,
    val description: String,
    val categoryAmountFormulasStr: String,
    val isAutoReplay: Boolean,
    val autoFillCategoryName: String,
)