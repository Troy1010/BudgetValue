package com.example.budgetvalue.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    var date: String?,
    var description: String,
    var amount: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)