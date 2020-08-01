package com.example.budgetvalue.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import java.math.BigDecimal

@Entity
data class Transaction(
    var date: String?,
    var description: String,
    var amount: String,
    var categoryAmounts: String = Gson().toJson(HashMap<String, BigDecimal>()),
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)