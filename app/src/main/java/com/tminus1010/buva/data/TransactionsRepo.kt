package com.tminus1010.buva.data

import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.environment.MiscDAO
import com.tminus1010.buva.environment.UserCategoriesDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class TransactionsRepo @Inject constructor(
    private val userCategoriesDAO: UserCategoriesDAO,
    private val miscDAO: MiscDAO,
) : MiscDAO by miscDAO {
    override fun fetchTransactions(): Flow<List<Transaction>> {
        return userCategoriesDAO.fetchUserCategories().flatMapLatest {
            miscDAO.fetchTransactions()
        }
    }
}