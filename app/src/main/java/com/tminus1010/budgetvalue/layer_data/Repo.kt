package com.tminus1010.budgetvalue.layer_data

import javax.inject.Inject

/**
 * A Repo is the facade to the data layer.
 * If you ever change how the data is written/retrieved, you don't need to change the ui_layer.
 */
class Repo @Inject constructor(
    transactionParser: TransactionParser,
    sharedPrefWrapper: ISharedPrefWrapper,
    myDaoWrapper: IMyDaoWrapper
) : ITransactionParser by transactionParser,
    ISharedPrefWrapper by sharedPrefWrapper,
    IMyDaoWrapper by myDaoWrapper