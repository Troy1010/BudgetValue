package com.example.budgetvalue.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.budgetvalue.layer_ui.misc.sum
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.collections.HashMap

@Entity
data class Transaction(
    var date: LocalDate,
    var description: String,
    var amount: BigDecimal,
    val categoryAmounts: HashMap<String, BigDecimal> = hashMapOf(),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
) {
    val isUncategorized: Boolean
        get() = categoryAmounts.isNullOrEmpty()
    val isSpend:Boolean
        get() = amount < BigDecimal.ZERO
    val uncategorizedAmounts: BigDecimal
        get() {
            return amount - categoryAmounts.values.sum()
        }
}