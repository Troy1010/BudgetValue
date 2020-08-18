package com.example.budgetvalue.models

import androidx.room.Entity

@Entity
data class Account (
    var name: String,
    var amount: String
)