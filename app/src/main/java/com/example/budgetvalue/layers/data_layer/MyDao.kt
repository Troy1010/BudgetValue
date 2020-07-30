package com.example.budgetvalue.layers.data_layer

import androidx.room.*
import java.math.BigDecimal
import com.example.budgetvalue.models.Transaction

@Dao
interface MyDao {
    @Query("DELETE FROM `Transaction`")
    fun clear()

    @Insert
    fun add(transaction: Transaction)

    @Query("select * from `Transaction`")
    fun getTransactions(): List<Transaction>

    @Delete
    fun deleteTransaction(transaction: Transaction)

    @Update
    fun updateTransaction(transaction: Transaction)

    @Query("SELECT COUNT(*) FROM `Transaction`")
    fun sizeZ(): Int
}