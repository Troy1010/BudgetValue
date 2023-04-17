package com.tminus1010.buva.app

import com.tminus1010.buva.environment.database_or_datastore_or_similar.MiscDAO
import com.tminus1010.buva.domain.Domain
import com.tminus1010.buva.domain.LocalDatePeriod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IsPeriodFullyImported @Inject constructor(
    private val miscDAO: MiscDAO,
) {
    val isPeriodFullyImportedLambda = miscDAO.fetchTransactionImportInfo().map {
        { datePeriod: LocalDatePeriod -> Domain.isPeriodFullyImported(datePeriod, it) }
    }

    suspend operator fun invoke(period: LocalDatePeriod): Flow<Boolean> {
        return miscDAO.fetchTransactionImportInfo().map { Domain.isPeriodFullyImported(period, it) }
    }
}
