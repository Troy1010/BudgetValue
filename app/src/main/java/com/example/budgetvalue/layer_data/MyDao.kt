package com.example.budgetvalue.layer_data

import androidx.room.*
import com.example.budgetvalue.model_data.Account
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_data.IncomeCategoryAmounts
import com.example.budgetvalue.model_data.PlanCategoryAmounts
import com.example.budgetvalue.model_data.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.math.BigDecimal

@Dao
interface MyDao {

    // # Transactions

    @Query("DELETE FROM `Transaction`")
    fun clearTransactions()

    @Insert
    fun add(transaction: Transaction)

    @Query("select * from `Transaction`")
    fun getTransactions(): Observable<List<Transaction>>

    @Delete
    fun delete(transaction: Transaction)

    @Update
    fun update(transaction: Transaction)

    fun add(transactions: List<Transaction>) {
        transactions.forEach { add(it) }
    }

    @Query("select date from `Transaction` WHERE id=:id")
    fun getTransactionDate(id: Int): Observable<String>

    @Query("UPDATE `Transaction` SET date=:date WHERE id=:id")
    fun updateTransactionDate(id: Int, date: String)

    @Query("UPDATE `Transaction` SET categoryAmounts=:categoryAmounts WHERE id=:id")
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

    // # IncomeCategoryAmounts

    @Query("select * from `IncomeCategoryAmounts`")
    fun getIncomeCategoryAmounts(): Observable<List<IncomeCategoryAmounts>>

    @Insert
    fun addIncomeCategoryAmount(incomeCategoryAmounts: IncomeCategoryAmounts)

    fun addIncomeCategoryAmount(category: Category) {
        addIncomeCategoryAmount(IncomeCategoryAmounts(category.name))
    }

    @Delete
    fun delete(incomeCategoryAmounts: IncomeCategoryAmounts)

    @Query("DELETE FROM `IncomeCategoryAmounts` WHERE category = :categoryName")
    fun deleteIncomeCategoryAmount(categoryName: String)

    fun deleteIncomeCategoryAmount(category: Category) {
        return deleteIncomeCategoryAmount(category.name)
    }

    @Update
    fun update(incomeCategoryAmounts: IncomeCategoryAmounts)

    // # PlanCategoryAmounts

    @Query("select * from `PlanCategoryAmounts`")
    fun getPlanCategoryAmounts(): Observable<List<PlanCategoryAmounts>>

    @Insert
    fun add(planCategoryAmounts: PlanCategoryAmounts)

    @Query("DELETE FROM `PlanCategoryAmounts`")
    fun clearPlanCategoryAmounts()

    @Update
    fun update(planCategoryAmounts: PlanCategoryAmounts)
}