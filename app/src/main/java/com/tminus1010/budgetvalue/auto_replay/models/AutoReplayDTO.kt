package com.tminus1010.budgetvalue.auto_replay.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.categories.models.Category
import java.math.BigDecimal

@Entity
data class AutoReplayDTO(
    @PrimaryKey
    val description: String,
    val categoryAmounts: String,
)
