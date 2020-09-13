package com.example.budgetvalue.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.budgetvalue.layers.z_ui.misc.sum
import com.google.gson.Gson
import java.math.BigDecimal

@Entity
data class Transaction(
    var date: String?,
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