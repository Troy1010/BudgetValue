package com.example.budgetvalue.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class Account (
    var name: String,
    var amount: BigDecimal,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)