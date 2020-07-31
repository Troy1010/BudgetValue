package com.example.budgetvalue.layers.data_layer

import androidx.room.*
import java.math.BigDecimal
import com.example.budgetvalue.models.Transaction

@Dao
interface MyDao {
    @Query("DELETE FROM `Transaction`")
    suspend fun clear()

    @Insert
    suspend fun add(transaction: Transaction)

    @Query("select * from `Transaction`")
    suspend fun getTransactions(): List<Transaction>

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("SELECT COUNT(*) FROM `Transaction`")
    suspend fun sizeZ(): Int

    suspend fun add(transactions: List<Transaction>) {
        for (transaction in transactions) {
            add(transaction)
        }
    }

    @Query("select date from `Transaction` WHERE id=:id")
    fun getTransactionDate(id: Int): String

    @Query("UPDATE `Transaction` SET date=:value WHERE id=:id")
    fun setTransactionDate(id: Int, value: String)
}