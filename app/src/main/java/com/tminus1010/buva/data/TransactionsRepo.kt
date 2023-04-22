package com.tminus1010.buva.data

import com.tminus1010.buva.all_layers.extensions.redoWhen
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.environment.adapter.MoshiWithCategoriesProvider
import com.tminus1010.buva.environment.room.MiscDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionsRepo @Inject constructor(
    private val moshiWithCategoriesProvider: MoshiWithCategoriesProvider,
    private val miscDAO: MiscDAO,
) : MiscDAO by miscDAO {
    override fun fetchTransactions(): Flow<List<Transaction>> {
        return miscDAO.fetchTransactions()
            .redoWhen(moshiWithCategoriesProvider.moshiFlow) // Room synchronously depends on moshiWithCategories, so we must redo when it emits.
    }

    val mostRecentImportItemDate =
        fetchTransactionImportInfo()
            .map { transactionImportInfos -> transactionImportInfos.maxByOrNull { it.period.endDate }?.period?.endDate }
}
