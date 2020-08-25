package com.example.budgetvalue.models

import androidx.room.Entity
import androidx.room.PrimaryKey
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
}