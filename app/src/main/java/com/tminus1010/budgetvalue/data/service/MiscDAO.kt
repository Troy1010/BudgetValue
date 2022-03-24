package com.tminus1010.budgetvalue.data.service

import androidx.room.*
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.LocalDatePeriod
import com.tminus1010.budgetvalue.domain.accounts.Account
import com.tminus1010.budgetvalue.domain.plan.Plan
import com.tminus1010.budgetvalue._unrestructured.reconcile.data.model.ReconciliationDTO
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.data.model.BasicReplayDTO
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue._unrestructured.replay_or_future.domain.TotalFuture
import com.tminus1010.budgetvalue._unrestructured.transactions.data.TransactionDTO
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

    @Query("DELETE FROM TransactionDTO")
    fun clearTransactions(): Completable

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun push(transactionDTO: TransactionDTO): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun tryAdd(transactionDTO: TransactionDTO): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun tryAdd(transactionsDTO: List<TransactionDTO>): Completable

    @Query("select * from TransactionDTO")
    fun fetchTransactionsFlow(): Flow<List<TransactionDTO>>

    @Query("select * from TransactionDTO")
    fun fetchTransactions(): Observable<List<TransactionDTO>>

    @Query("select * from TransactionDTO WHERE description=:description")
    fun fetchTransactions(description: String): Single<List<TransactionDTO>>

    @Delete
    fun delete(transaction: TransactionDTO): Completable

    @Deprecated("Use update2")
    @Update
    fun update(transaction: TransactionDTO): Completable

    @Update
    suspend fun update2(transaction: TransactionDTO)

    @Query("select * from TransactionDTO WHERE id=:id")
    fun getTransaction(id: String): Single<TransactionDTO>

    @Query("select * from TransactionDTO WHERE id=:id")
    suspend fun getTransaction2(id: String): TransactionDTO

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

    @Query("select * from ReconciliationDTO")
    fun fetchReconciliations(): Observable<List<ReconciliationDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun push(reconciliationDTO: ReconciliationDTO): Completable

    @Update
    fun update(reconciliationDTO: ReconciliationDTO): Completable

    @Delete
    fun delete(reconciliationDTO: ReconciliationDTO): Completable

    @Query("DELETE FROM ReconciliationDTO")
    fun clearReconciliations(): Completable

    // # Replays

    @Query("select * from BasicReplayDTO")
    fun fetchBasicReplays(): Observable<List<BasicReplayDTO>>

    @Insert
    fun push(basicReplayDTO: BasicReplayDTO): Completable

    @Query("DELETE FROM BasicReplayDTO WHERE name=:basicReplayName")
    fun delete(basicReplayName: String): Completable

    @Update
    fun update(basicReplayDTO: BasicReplayDTO): Completable

    // # Futures

    @Query("select * from BasicFuture")
    fun fetchBasicFutures(): Flow<List<BasicFuture>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(basicFuture: BasicFuture)

    @Query("DELETE FROM BasicFuture WHERE name=:name")
    suspend fun deleteBasicFuture(name: String)

    @Query("select * from TotalFuture")
    fun fetchTotalFutures(): Flow<List<TotalFuture>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(totalFuture: TotalFuture)

    @Query("DELETE FROM TotalFuture WHERE name=:name")
    suspend fun deleteTotalFuture(name: String)
}