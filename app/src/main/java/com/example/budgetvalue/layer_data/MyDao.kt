package com.example.budgetvalue.layer_data

import androidx.room.*
import com.example.budgetvalue.model_data.Account
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_data.ReconcileCategoryAmounts
import com.example.budgetvalue.model_data.PlanCategoryAmounts
import com.example.budgetvalue.model_data.TransactionReceived
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal

@Dao
interface MyDao {

    // # Transactions

    @Query("DELETE FROM `TransactionReceived`")
    fun clearTransactions()

    @Insert
    fun add(transaction: TransactionReceived)

    @Query("select * from `TransactionReceived`")
    fun getTransactions(): Observable<List<TransactionReceived>>

    @Delete
    fun delete(transaction: TransactionReceived)

    @Update
    fun update(transaction: TransactionReceived)

    fun add(transactions: List<TransactionReceived>) {
        transactions.forEach { add(it) }
    }

    @Query("select date from `TransactionReceived` WHERE id=:id")
    fun getTransactionDate(id: Int): Observable<String>

    @Query("UPDATE `TransactionReceived` SET date=:date WHERE id=:id")
    fun updateTransactionDate(id: Int, date: String)

    @Query("UPDATE `TransactionReceived` SET categoryAmounts=:categoryAmounts WHERE id=:id")
    fun updateTransactionCategoryAmounts(id: Int, categoryAmounts: HashMap<String, BigDecimal>)

    // # Accounts

    @Query("select * from `Account`")
    fun getAccounts(): Observable<List<Account>>

    @Query("select * from `Account` where id=:id")
    fun getAccount(id: Int) : Observable<Account>

    @Insert
    fun add(account: Account)

    @Delete
    fun delete(account: Account)

    @Update
    fun _update(account: Account) : Completable

    fun update(account: Account): Completable {
        return getAccount(account.id)
            .take(1)
            .flatMapCompletable {
                if (it == account)
                    Completable.fromAction {  }
                else
                    _update(account).observeOn(Schedulers.io()).subscribeOn(Schedulers.io())
            }// TODO("Simplify")
    }

    // # ReconcileCategoryAmounts

    @Query("select * from ReconcileCategoryAmounts")
    fun getReconcileCategoryAmount(): Observable<List<ReconcileCategoryAmounts>>

    @Insert
    fun add(reconcileCategoryAmounts: ReconcileCategoryAmounts)

    fun addReconcileCategoryAmount(category: Category) {
        add(ReconcileCategoryAmounts(category.name))
    }

    @Delete
    fun delete(reconcileCategoryAmounts: ReconcileCategoryAmounts)

    @Query("DELETE FROM ReconcileCategoryAmounts WHERE categoryName = :categoryName")
    fun deleteReconcileCategoryAmount(categoryName: String)

    fun deleteReconcileCategoryAmount(category: Category) {
        return deleteReconcileCategoryAmount(category.name)
    }

    @Update
    fun update(reconcileCategoryAmounts: ReconcileCategoryAmounts)

    // # PlanCategoryAmounts

    @Query("select * from `PlanCategoryAmounts`")
    fun getPlanCategoryAmounts(): Observable<List<PlanCategoryAmounts>>

    @Insert
    fun add(planCategoryAmounts: PlanCategoryAmounts)

    @Query("DELETE FROM `PlanCategoryAmounts`")
    fun clearPlanCategoryAmounts() : Completable

    @Update
    fun update(planCategoryAmounts: PlanCategoryAmounts)
}