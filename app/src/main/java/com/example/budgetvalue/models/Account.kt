package com.example.budgetvalue.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Account (
    @PrimaryKey
    var name: String,
    var amount: String
)