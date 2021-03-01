package com.tminus1010.budgetvalue.layer_data

import androidx.room.*
import com.tminus1010.budgetvalue.model_data.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import java.math.BigDecimal
import java.time.LocalDate

@Dao
interface MiscDAO {
    // # Accounts

    @Query("DELETE FROM AccountDTO")
    fun clearAccounts(): Completable

    @Query("select * from AccountDTO")
    fun fetchAccounts(): Observable<List<AccountDTO>>

    @Query("select * from AccountDTO where id=:id")
    fun getAccount(id: Int): Observable<AccountDTO>

    @Insert
    fun addAccount(accountDTO: AccountDTO): Completable

    @Delete
    fun deleteAccount(accountDTO: AccountDTO): Completable

    @Update
    fun updateAccount(accountDTO: AccountDTO): Completable

    // # Transactions

    @Query("DELETE FROM `TransactionReceived`")
    fun clearTransactions(): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun tryAdd(transactionReceived: TransactionReceived): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun tryAdd(transactionsReceived: List<TransactionReceived>): Completable

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
    fun updateTransactionCategoryAmounts(id: String, categoryAmounts: Map<String, BigDecimal>): Completable

    // # PlanCategoryAmounts

    @Query("select * from PlanReceived")
    fun fetchPlanReceived(): Observable<List<PlanReceived>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(PlanReceived: PlanReceived): Completable

    @Update
    fun update(PlanReceived: PlanReceived): Completable

    @Query("DELETE FROM PlanReceived")
    fun clearPlans(): Completable

    @Query("UPDATE `PlanReceived` SET categoryAmounts=:categoryAmounts WHERE startDate=:startDate")
    fun updatePlanCategoryAmounts(startDate: LocalDate, categoryAmounts: Map<String, BigDecimal>): Completable

    // # Reconciliations

    @Query("select * from ReconciliationReceived")
    fun fetchReconciliationReceived(): Observable<List<ReconciliationReceived>>

    @Insert
    fun add(reconciliationReceived: ReconciliationReceived): Completable

    @Update
    fun update(reconciliationReceived: ReconciliationReceived): Completable

    @Query("DELETE FROM ReconciliationReceived")
    fun clearReconciliations(): Completable

    @Query("UPDATE `ReconciliationReceived` SET categoryAmounts=:categoryAmounts WHERE id=:id")
    fun updateReconciliationCategoryAmounts(id: Int, categoryAmounts: Map<String, BigDecimal>): Completable
}