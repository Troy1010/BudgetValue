package com.tminus1010.budgetvalue._core.data

import androidx.room.*
import com.tminus1010.budgetvalue._core.domain.CategoryAmounts
import com.tminus1010.budgetvalue._core.domain.LocalDatePeriod
import com.tminus1010.budgetvalue.accounts.data.AccountDTO
import com.tminus1010.budgetvalue.plans.domain.Plan
import com.tminus1010.budgetvalue.reconcile.data.model.ReconciliationDTO
import com.tminus1010.budgetvalue.replay_or_future.data.model.BasicReplayDTO
import com.tminus1010.budgetvalue.replay_or_future.data.model.TotalFutureDTO
import com.tminus1010.budgetvalue.replay_or_future.domain.BasicFuture
import com.tminus1010.budgetvalue.transactions.data.TransactionDTO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

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
    fun update(accountDTO: AccountDTO): Completable

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

    @Query("UPDATE TransactionDTO SET categoryAmounts=:categoryAmounts WHERE id=:id")
    fun updateTransactionCategoryAmounts(id: String, categoryAmounts: Map<String, BigDecimal>): Completable

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

    @Query("UPDATE ReconciliationDTO SET categoryAmounts=:categoryAmounts WHERE id=:id")
    fun updateReconciliationCategoryAmounts(id: Int, categoryAmounts: Map<String, BigDecimal>): Completable

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
    fun fetchBasicFutures(): Observable<List<BasicFuture>>

    @Insert
    fun push(basicFutureDTO: BasicFuture): Completable

    @Query("DELETE FROM BasicFuture WHERE name=:name")
    fun deleteBasicFuture(name: String): Completable

    @Update
    fun update(basicFutureDTO: BasicFuture): Completable

    @Query("select * from TotalFutureDTO")
    fun fetchTotalFutures(): Observable<List<TotalFutureDTO>>

    @Insert
    fun push(totalFutureDTO: TotalFutureDTO): Completable

    @Query("DELETE FROM TotalFutureDTO WHERE name=:name")
    fun deleteTotalFuture(name: String): Completable

    @Update
    fun update(totalFutureDTO: TotalFutureDTO): Completable
}