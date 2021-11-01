package com.tminus1010.budgetvalue.categories.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.tminus1010.budgetvalue._core.data.RoomTypeConverter
import com.tminus1010.budgetvalue.transactions.app.AmountFormula
import java.math.BigDecimal

@Entity
data class Category(
    @PrimaryKey
    val name: String,
    val type: CategoryType = CategoryType.Always,
    @TypeConverters(RoomTypeConverter::class)
    val defaultAmountFormula: AmountFormula = AmountFormula.Value(BigDecimal.ZERO),
    val isRequired: Boolean = false,
) {
    override fun toString() = name // for logs
    fun toDTO() = CategoryDTO(name, type.ordinal, defaultAmountFormula.toDTO(), isRequired)

    companion object {
        fun fromDTO(categoryDTO: CategoryDTO): Category {
            return Category(
                categoryDTO.name,
                CategoryType.values()[categoryDTO.type],
                AmountFormula.fromDTO(categoryDTO.defaultAmountFormulaStr),
                categoryDTO.isRequired,
            )
        }
    }
}