package com.tminus1010.budgetvalue.replay_or_future.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStatus

@Entity
data class BasicFutureDTO(
    @PrimaryKey
    val name: String,
    val searchTexts: List<String>,
    val categoryAmountFormulasStr: String,
    val autoFillCategory: Category,
    val terminationStatus: TerminationStatus,
)