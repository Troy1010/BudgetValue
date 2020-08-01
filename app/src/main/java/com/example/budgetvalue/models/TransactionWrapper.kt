package com.example.budgetvalue.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class TransactionWrapper(
    private val transaction: Transaction
) {
    fun getDate() {

    }
}