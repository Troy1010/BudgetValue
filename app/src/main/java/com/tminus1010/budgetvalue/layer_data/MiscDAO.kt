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

    @Query("DELETE FROM TransactionDTO")
    fun clearTransactions(): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun tryAdd(transactionDTO: TransactionDTO): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun tryAdd(transactionsDTO: List<TransactionDTO>): Completable

    @Query("select * from TransactionDTO")
    fun getTransactionsReceived(): Observable<List<TransactionDTO>>

    @Delete
    fun delete(transaction: TransactionDTO): Completable

    @Update
    fun update(transaction: TransactionDTO): Completable

    @Query("select date from TransactionDTO WHERE id=:id")
    fun getTransactionDate(id: Int): Observable<String>

    @Query("UPDATE TransactionDTO SET date=:date WHERE id=:id")
    fun updateTransactionDate(id: Int, date: String): Completable

    @Query("UPDATE TransactionDTO SET categoryAmounts=:categoryAmounts WHERE id=:id")
    fun updateTransactionCategoryAmounts(id: String, categoryAmounts: Map<String, BigDecimal>): Completable

    // # PlanCategoryAmounts

    @Query("select * from PlanDTO")
    fun fetchPlanReceived(): Observable<List<PlanDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(PlanDTO: PlanDTO): Completable

    @Update
    fun update(PlanDTO: PlanDTO): Completable

    @Query("DELETE FROM PlanDTO")
    fun clearPlans(): Completable

    @Query("UPDATE PlanDTO SET categoryAmounts=:categoryAmounts WHERE startDate=:startDate")
    fun updatePlanCategoryAmounts(startDate: LocalDate, categoryAmounts: Map<String, BigDecimal>): Completable

    // # Reconciliations

    @Query("select * from ReconciliationDTO")
    fun fetchReconciliationReceived(): Observable<List<ReconciliationDTO>>

    @Insert
    fun add(reconciliationDTO: ReconciliationDTO): Completable

    @Update
    fun update(reconciliationDTO: ReconciliationDTO): Completable

    @Query("DELETE FROM ReconciliationDTO")
    fun clearReconciliations(): Completable

    @Query("UPDATE ReconciliationDTO SET categoryAmounts=:categoryAmounts WHERE id=:id")
    fun updateReconciliationCategoryAmounts(id: Int, categoryAmounts: Map<String, BigDecimal>): Completable
}