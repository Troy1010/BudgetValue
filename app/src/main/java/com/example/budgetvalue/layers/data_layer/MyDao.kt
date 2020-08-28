package com.example.budgetvalue.layers.data_layer

import androidx.room.*
import com.example.budgetvalue.models.Account
import com.example.budgetvalue.models.Transaction
import io.reactivex.rxjava3.core.Observable

@Dao
interface MyDao {
    @Query("DELETE FROM `Transaction`")
    suspend fun clear()

    @Insert
    suspend fun addTransaction(transaction: Transaction)

    @Query("select * from `Transaction`")
    fun getTransactions(): Observable<List<Transaction>>

    @Query("select * from `Account`")
    fun getAccounts(): Observable<List<Account>>

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("SELECT COUNT(*) FROM `Transaction`")
    suspend fun sizeZ(): Int

    suspend fun addTransaction(transactions: List<Transaction>) {
        for (transaction in transactions) {
            addTransaction(transaction)
        }
    }

    @Query("select date from `Transaction` WHERE id=:id")
    fun getTransactionDate(id: Int): String

    @Query("UPDATE `Transaction` SET date=:value WHERE id=:id")
    fun setTransactionDate(id: Int, value: String)

    @Insert
    suspend fun addAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)

    @Update
    suspend fun updateAccount(account: Account)
}