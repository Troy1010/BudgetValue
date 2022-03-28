package com.tminus1010.budgetvalue.data.service

import androidx.room.*
import com.tminus1010.budgetvalue._unrestructured.reconcile.domain.Reconciliation
import com.tminus1010.budgetvalue._unrestructured.transactions.data.TransactionDTO
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.Future
import com.tminus1010.budgetvalue.domain.LocalDatePeriod
import com.tminus1010.budgetvalue.domain.accounts.Account
import com.tminus1010.budgetvalue.domain.plan.Plan
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface MiscDAO {
    // # Accounts
    @Query("DELETE FROM Account")
    suspend fun clearAccounts()

    @Query("select * from Account")
    fun fetchAccounts(): Flow<List<Account>>

    @Query("select * from Account where id=:id")
    suspend fun getAccount(id: Int): Account

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: Account)

    @Delete
    suspend fun delete(account: Account)

    @Update
    suspend fun update(account: Account)

    // # Transactions

    @Query("select * from TransactionDTO")
    fun fetchTransactionsFlow(): Flow<List<TransactionDTO>>

    @Query("DELETE FROM TransactionDTO")
    fun clearTransactions(): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(transactionDTO: TransactionDTO)

    @Query("DELETE FROM TransactionDTO WHERE id=:id")
    suspend fun deleteTransaction(id: String)

    @Update
    suspend fun update(transaction: TransactionDTO)

    @Query("select * from TransactionDTO WHERE id=:id")
    suspend fun getTransaction(id: String): TransactionDTO?

    // # Plan

    @Query("select * from `Plan`")
    fun getPlans(): Flow<List<Plan>>

    @Query("select * from `Plan` WHERE localDatePeriod=:localDatePeriod")
    suspend fun getPlan(localDatePeriod: LocalDatePeriod): Plan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: Plan)

    @Update
    suspend fun update(plan: Plan)

    @Delete
    suspend fun delete(plan: Plan)

    @Query("DELETE FROM `Plan`")
    suspend fun clearPlans2()

    @Query("UPDATE `Plan` SET categoryAmounts=:categoryAmounts WHERE localDatePeriod=:localDatePeriod")
    suspend fun updatePlanCategoryAmounts(localDatePeriod: LocalDatePeriod, categoryAmounts: CategoryAmounts)

    @Query("UPDATE `Plan` SET total=:total WHERE localDatePeriod=:localDatePeriod")
    suspend fun updatePlanAmount(localDatePeriod: LocalDatePeriod, total: BigDecimal)

    // # Reconciliations

    @Query("select * from Reconciliation")
    fun fetchReconciliations(): Observable<List<Reconciliation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun push(reconciliation: Reconciliation): Completable

    @Delete
    fun delete(reconciliation: Reconciliation): Completable

    // # Futures

    @Query("select * from Future")
    fun fetchFutures(): Flow<List<Future>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(future: Future)

    @Query("DELETE FROM Future WHERE name=:name")
    suspend fun deleteFuture(name: String)
}