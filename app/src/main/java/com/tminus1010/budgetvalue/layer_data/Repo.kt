package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_data.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A Repo is the facade to the data layer.
 * If you ever change how the data is written/retrieved, all other layers will not require updates.
 */
@Singleton
class Repo @Inject constructor(
    transactionParser: TransactionParser,
    sharedPrefWrapper: SharedPrefWrapper,
    miscDAOWrapper: MiscDAOWrapper,
    activeCategoryDAOWrapper: ActiveCategoriesDAOWrapper,
) : ITransactionParser by transactionParser,
    ISharedPrefWrapper by sharedPrefWrapper,
    IMiscDAOWrapper by miscDAOWrapper,
    IActiveCategoriesDAOWrapper by activeCategoryDAOWrapper {
    fun deleteFromActive(category: Category) {
        pushActivePlanCA(Pair(category, null))
        pushActiveReconciliationCA(Pair(category, null))
        delete(category).launch()
    }

    fun deleteFromEverywhere(category: Category) {
        deleteFromActive(category)
        transactions
            .take(1)
            .subscribe { it.forEach { pushTransactionCA(it, category, null) } }
        reconciliations
            .take(1)
            .subscribe { it.forEach { pushReconciliationCA(it, category, null) } }
        plans
            .take(1)
            .subscribe { it.forEach { pushPlanCA(it, category, null) } }
    }
}