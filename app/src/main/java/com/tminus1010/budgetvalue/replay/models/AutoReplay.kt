package com.tminus1010.budgetvalue.replay.models

import com.tminus1010.budgetvalue.categories.CategoryAmountsConverter
import com.tminus1010.budgetvalue.categories.models.Category
import java.math.BigDecimal

data class AutoReplay(
    val description: String,
    val categoryAmounts: Map<Category, BigDecimal>,
) {
    fun toDTO(categoryAmountsConverter: CategoryAmountsConverter): AutoReplayDTO {
        return AutoReplayDTO(
            description,
            categoryAmountsConverter.toJson(categoryAmounts),
        )
    }

    companion object {
        fun fromDTO(autoReplayDTO: AutoReplayDTO, categoryAmountsConverter: CategoryAmountsConverter) = autoReplayDTO.run {
            AutoReplay(
                description,
                categoryAmountsConverter.toCategoryAmounts(categoryAmounts),
            )
        }
    }
}
