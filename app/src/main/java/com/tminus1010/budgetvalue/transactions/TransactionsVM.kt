package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue.transactions.domain.ITransactionsDomain
import com.tminus1010.budgetvalue.transactions.domain.TransactionsDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    transactionsDomain: TransactionsDomain
) : ViewModel(), ITransactionsDomain by transactionsDomain