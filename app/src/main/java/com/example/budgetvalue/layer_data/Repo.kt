package com.example.budgetvalue.layer_data

import javax.inject.Inject

class Repo @Inject constructor(
    transactionParser: TransactionParser,
    sharedPrefWrapper: ISharedPrefWrapper,
    val myDao: MyDao
) : ITransactionParser by transactionParser,
    ISharedPrefWrapper by sharedPrefWrapper,
    MyDao by myDao