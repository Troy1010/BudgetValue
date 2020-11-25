package com.example.budgetvalue.layer_data

import androidx.room.*
import com.example.budgetvalue.model_data.Account
import com.example.budgetvalue.model_data.PlanCategoryAmount
import com.example.budgetvalue.model_data.TransactionReceived
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal

@Dao
interface MyDao {

    // # Transactions

    @Query("DELETE FROM `TransactionReceived`")
    fun clearTransactions(): Completable

    @Insert
    fun add(transactionReceived: TransactionReceived): Completable

    @Insert
    fun add(transactionsReceived: List<TransactionReceived>): Completable

    @Query("select * from `TransactionReceived`")
    fun getTransactionsReceived(): Observable<List<TransactionReceived>>

    @Delete
    fun delete(transaction: TransactionReceived): Completable

    @Update
    fun update(transaction: TransactionReceived): Completable

    @Query("select date from `TransactionReceived` WHERE id=:id")
    fun getTransactionDate(id: Int): Observable<String>

    @Query("UPDATE `TransactionReceived` SET date=:date WHERE id=:id")
    fun updateTransactionDate(id: Int, date: String): Completable

    @Query("UPDATE `TransactionReceived` SET categoryAmounts=:categoryAmounts WHERE id=:id")
    fun updateTransactionCategoryAmounts(id: Int, categoryAmounts: HashMap<String, BigDecimal>): Completable

    // # Accounts

    @Query("select * from `Account`")
    fun getAccounts(): Observable<List<Account>>

    @Query("select * from `Account` where id=:id")
    fun getAccount(id: Int): Observable<Account>

    @Insert
    fun add(account: Account): Completable

    @Delete
    fun delete(account: Account): Completable

    @Update
    fun update(account: Account): Completable

    // # PlanCategoryAmounts

    @Query("select * from PlanCategoryAmount")
    fun getPlanCategoryAmountsReceived(): Observable<List<PlanCategoryAmount>>

    @Insert
    fun add(planCategoryAmount: PlanCategoryAmount): Completable

    @Query("DELETE FROM PlanCategoryAmount")
    fun clearPlanCategoryAmounts(): Completable

    @Update
    fun update(planCategoryAmount: PlanCategoryAmount): Completable
}