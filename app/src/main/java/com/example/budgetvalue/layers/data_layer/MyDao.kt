package com.example.budgetvalue.layers.data_layer

import androidx.room.*
import com.example.budgetvalue.models.Account
import com.example.budgetvalue.models.IncomeCategoryAmounts
import com.example.budgetvalue.models.Transaction
import io.reactivex.rxjava3.core.Observable

@Dao
interface MyDao {

    // Transactions

    @Query("DELETE FROM `Transaction`")
    suspend fun clear()

    @Insert
    suspend fun addTransaction(transaction: Transaction)

    @Query("select * from `Transaction`")
    fun getTransactions(): Observable<List<Transaction>>

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

    // Accounts

    @Query("select * from `Account`")
    fun getAccounts(): Observable<List<Account>>

    @Query("select * from `Account` where id=:id")
    suspend fun getAccount(id: Int) : Account

    @Insert
    suspend fun addAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)

    @Update
    suspend fun updateAccount(account: Account)

    // IncomeCategoryAmounts

    @Query("select * from `IncomeCategoryAmounts`")
    fun getIncomeCategoryAmounts(): Observable<List<IncomeCategoryAmounts>>

    @Insert
    suspend fun addIncomeCategoryAmount(incomeCategoryAmounts: IncomeCategoryAmounts)

    @Delete
    suspend fun deleteIncomeCategoryAmount(incomeCategoryAmounts: IncomeCategoryAmounts)

    @Update
    suspend fun updateIncomeCategoryAmount(incomeCategoryAmounts: IncomeCategoryAmounts)
}