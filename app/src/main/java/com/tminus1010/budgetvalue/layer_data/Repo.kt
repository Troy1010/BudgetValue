package com.tminus1010.budgetvalue.layer_data

import com.tminus1010.budgetvalue.model_data.Category
import javax.inject.Inject

/**
 * A Repo is the facade to the data layer.
 * If you ever change how the data is written/retrieved, you don't need to change the ui_layer.
 */
class Repo @Inject constructor(
    transactionParser: TransactionParser,
    sharedPrefWrapper: ISharedPrefWrapper,
    myDaoWrapper: IMyDaoWrapper,
    activeCategoryDAOWrapper: IActiveCategoryDAOWrapper,
) : ITransactionParser by transactionParser,
    ISharedPrefWrapper by sharedPrefWrapper,
    IMyDaoWrapper by myDaoWrapper,
    IActiveCategoryDAOWrapper by activeCategoryDAOWrapper {
    fun deleteFromActive(category: Category) {
        pushActivePlanCA(Pair(category, null))
        pushActiveReconcileCA(Pair(category, null))
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