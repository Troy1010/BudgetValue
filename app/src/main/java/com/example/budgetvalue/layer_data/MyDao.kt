package com.example.budgetvalue.layer_data

import androidx.room.*
import com.example.budgetvalue.model_data.Account
import com.example.budgetvalue.model_app.Category
import com.example.budgetvalue.model_data.IncomeCategoryAmounts
import com.example.budgetvalue.model_data.PlanCategoryAmounts
import com.example.budgetvalue.model_data.Transaction
import io.reactivex.rxjava3.core.Observable

@Dao
interface MyDao {

    // Transactions

    @Query("DELETE FROM `Transaction`")
    suspend fun clear()

    @Insert
    suspend fun addTransaction(transaction: Transaction)

    @Query("select * from `Transaction`")
    fun getTransactions(): Observable<List<Transaction>>

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Query("SELECT COUNT(*) FROM `Transaction`")
    suspend fun sizeZ(): Int

    suspend fun addTransaction(transactions: List<Transaction>) {
        for (transaction in transactions) {
            addTransaction(transaction)
        }
    }

    @Query("select date from `Transaction` WHERE id=:id")
    fun getTransactionDate(id: Int): String

    @Query("UPDATE `Transaction` SET date=:value WHERE id=:id")
    fun setTransactionDate(id: Int, value: String)

    // Accounts

    @Query("select * from `Account`")
    fun getAccounts(): Observable<List<Account>>

    @Query("select * from `Account` where id=:id")
    suspend fun getAccount(id: Int) : Account

    @Insert
    suspend fun addAccount(account: Account)

    @Delete
    suspend fun deleteAccount(account: Account)

    @Update
    suspend fun updateAccount(account: Account)

    // IncomeCategoryAmounts

    @Query("select * from `IncomeCategoryAmounts`")
    fun getIncomeCategoryAmounts(): Observable<List<IncomeCategoryAmounts>>

    @Insert
    suspend fun addIncomeCategoryAmount(incomeCategoryAmounts: IncomeCategoryAmounts)

    suspend fun addIncomeCategoryAmount(category: Category) {
        addIncomeCategoryAmount(IncomeCategoryAmounts(category.name))
    }

    @Delete
    suspend fun deleteIncomeCategoryAmount(incomeCategoryAmounts: IncomeCategoryAmounts)

    @Query("DELETE FROM `IncomeCategoryAmounts` WHERE category = :categoryName")
    suspend fun deleteIncomeCategoryAmount(categoryName: String)

    suspend fun deleteIncomeCategoryAmount(category: Category) {
        return deleteIncomeCategoryAmount(category.name)
    }

    @Update
    suspend fun updateIncomeCategoryAmount(incomeCategoryAmounts: IncomeCategoryAmounts)

    @Query("select * from `IncomeCategoryAmounts` where category == :categoryName")
    suspend fun howManyIncomeCategoryAmountHaveCategory(categoryName: String): List<IncomeCategoryAmounts>

    suspend fun doesIncomeCategoryAmountHaveCategory(category: Category): Boolean {
        return howManyIncomeCategoryAmountHaveCategory(category.name).isNotEmpty() // TODO could be more performant
    }

    // # PlanCategoryAmounts

    @Query("select * from `PlanCategoryAmounts`")
    fun getPlanCategoryAmounts(): Observable<List<PlanCategoryAmounts>>

    @Insert
    fun addPlanCategoryAmounts(planCategoryAmounts: PlanCategoryAmounts)

    @Query("DELETE FROM `IncomeCategoryAmounts`")
    suspend fun deleteAllPlanCategoryAmounts()
}