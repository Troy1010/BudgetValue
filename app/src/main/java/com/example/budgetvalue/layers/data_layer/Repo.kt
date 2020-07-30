package com.example.budgetvalue.layers.data_layer

import javax.inject.Inject

class Repo @Inject constructor(
    transactionParser: TransactionParser
) : ITransactionParser by transactionParser