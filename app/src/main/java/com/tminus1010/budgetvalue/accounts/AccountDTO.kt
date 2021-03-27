package com.tminus1010.budgetvalue.accounts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountDTO(
    var name: String,
    var amount: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)