package com.example.budgetvalue.layers.data_layer

import javax.inject.Inject

class Repo @Inject constructor(
    transactionParser: TransactionParser,
    val myDao: MyDao
) : ITransactionParser by transactionParser,
    MyDao by myDao