package com.tminus1010.budgetvalue.replay.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AutoReplayDTO(
    @PrimaryKey
    val description: String,
    val categoryAmounts: String,
)
