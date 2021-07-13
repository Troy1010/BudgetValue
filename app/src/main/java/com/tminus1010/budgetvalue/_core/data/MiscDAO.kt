package com.tminus1010.budgetvalue._core.data

import androidx.room.*
import com.tminus1010.budgetvalue.accounts.models.AccountDTO
import com.tminus1010.budgetvalue.plans.models.PlanDTO
import com.tminus1010.budgetvalue.reconciliations.models.ReconciliationDTO
import com.tminus1010.budgetvalue.replay.models.BasicFutureDTO
import com.tminus1010.budgetvalue.replay.models.BasicReplayDTO
import com.tminus1010.budgetvalue.transactions.models.TransactionDTO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
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
    fun update(accountDTO: AccountDTO): Completable

    // # Transactions

    @Query("DELETE FROM TransactionDTO")
    fun clearTransactions(): Completable

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun add(transactionDTO: TransactionDTO): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun tryAdd(transactionDTO: TransactionDTO): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun tryAdd(transactionsDTO: List<TransactionDTO>): Completable

    @Query("select * from TransactionDTO")
    fun fetchTransactions(): Observable<List<TransactionDTO>>

    @Query("select * from TransactionDTO WHERE description=:description")
    fun fetchTransactions(description: String): Single<List<TransactionDTO>>

    @Delete
    fun delete(transaction: TransactionDTO): Completable

    @Update
    fun update(transaction: TransactionDTO): Completable

    @Query("select * from TransactionDTO WHERE id=:id")
    fun getTransaction(id: String): Single<TransactionDTO>

    @Query("UPDATE TransactionDTO SET categoryAmounts=:categoryAmounts WHERE id=:id")
    fun updateTransactionCategoryAmounts(id: String, categoryAmounts: Map<String, BigDecimal>): Completable

    // # PlanCategoryAmounts

    @Query("select * from PlanDTO")
    fun fetchPlans(): Observable<List<PlanDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(planDTO: PlanDTO): Completable

    @Update
    fun update(planDTO: PlanDTO): Completable

    @Delete
    fun delete(planDTO: PlanDTO): Completable

    @Query("DELETE FROM PlanDTO")
    fun clearPlans(): Completable

    @Query("UPDATE PlanDTO SET categoryAmounts=:categoryAmounts WHERE startDate=:startDate")
    fun updatePlanCategoryAmounts(startDate: LocalDate, categoryAmounts: Map<String, BigDecimal>): Completable

    @Query("UPDATE PlanDTO SET amount=:amount WHERE startDate=:startDate")
    fun updatePlanAmount(startDate: LocalDate, amount: BigDecimal): Completable

    // # Reconciliations

    @Query("select * from ReconciliationDTO")
    fun fetchReconciliations(): Observable<List<ReconciliationDTO>>

    @Insert
    fun add(reconciliationDTO: ReconciliationDTO): Completable

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
    fun add(basicReplayDTO: BasicReplayDTO): Completable

    @Query("DELETE FROM BasicReplayDTO WHERE name=:basicReplayName")
    fun delete(basicReplayName: String): Completable

    // # Futures

    @Query("select * from BasicFutureDTO")
    fun fetchBasicFutures(): Observable<List<BasicFutureDTO>>

    @Insert
    fun add(basicFutureDTO: BasicFutureDTO): Completable

    @Query("DELETE FROM BasicFutureDTO WHERE name=:basicFutureName")
    fun deleteFuture(basicFutureName: String): Completable
}