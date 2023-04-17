package com.tminus1010.buva.environment.database_or_datastore_or_similar

import androidx.room.*
import com.tminus1010.buva.domain.*
import com.tminus1010.buva.domain.Transaction
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface MiscDAO {
    /**
     * [ReconciliationSkip]
     */
    @Query("select * from ReconciliationSkip")
    fun fetchReconciliationSkips(): Flow<List<ReconciliationSkip>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reconciliationSkip: ReconciliationSkip)

    @Delete
    suspend fun delete(reconciliationSkip: ReconciliationSkip)

    @Delete
    suspend fun delete(reconciliationSkips: List<ReconciliationSkip>)

    /**
     * [Account]
     */
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

    /**
     * [Transaction]
     */
    @Query("select * from `Transaction`")
    fun fetchTransactions(): Flow<List<Transaction>>

    @Query("DELETE FROM `Transaction`")
    suspend fun clearTransactions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(transaction: List<Transaction>)

    @Query("DELETE FROM `Transaction` WHERE id=:id")
    suspend fun deleteTransaction(id: String)

    @Query("select * from `Transaction` WHERE id=:id")
    suspend fun getTransaction(id: String): Transaction?

    /**
     * [TransactionImportInfo]
     */
    @Query("select * from `TransactionImportInfo`")
    fun fetchTransactionImportInfo(): Flow<List<TransactionImportInfo>>

    @Query("DELETE FROM `TransactionImportInfo`")
    suspend fun clearTransactionImportInfo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(transactionImportInfo: TransactionImportInfo)

    /**
     * [AccountsUpdateInfo]
     */
    @Query("select * from `AccountsUpdateInfo`")
    fun fetchAccountsUpdateInfo(): Flow<List<AccountsUpdateInfo>>

    @Query("DELETE FROM `AccountsUpdateInfo`")
    suspend fun clearAccountsUpdateInfo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(accountsUpdateInfo: AccountsUpdateInfo)

    /**
     * [Plan]
     */
    @Query("select * from `Plan`")
    fun fetchPlans(): Flow<List<Plan>>

    @Query("select * from `Plan` WHERE localDatePeriod=:localDatePeriod")
    suspend fun getPlan(localDatePeriod: LocalDatePeriod): Plan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plan: Plan)

    @Update
    suspend fun update(plan: Plan)

    @Delete
    suspend fun delete(plan: Plan)

    @Query("UPDATE `Plan` SET categoryAmounts=:categoryAmounts WHERE localDatePeriod=:localDatePeriod")
    suspend fun updatePlanCategoryAmounts(localDatePeriod: LocalDatePeriod, categoryAmounts: CategoryAmounts)

    @Query("UPDATE `Plan` SET total=:total WHERE localDatePeriod=:localDatePeriod")
    suspend fun updatePlanAmount(localDatePeriod: LocalDatePeriod, total: BigDecimal)

    /**
     * [Reconciliation]
     */
    @Query("select * from Reconciliation")
    fun fetchReconciliations(): Flow<List<Reconciliation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(reconciliation: Reconciliation)

    @Delete
    suspend fun delete(reconciliation: Reconciliation)

    /**
     * [Future]
     */
    @Query("select * from Future")
    fun fetchFutures(): Flow<List<Future>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun push(future: Future)

    @Query("DELETE FROM Future WHERE name=:name")
    suspend fun deleteFuture(name: String)
}