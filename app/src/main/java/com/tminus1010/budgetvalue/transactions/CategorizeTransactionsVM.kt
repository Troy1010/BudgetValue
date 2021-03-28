package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategorizeTransactionsVM @Inject constructor(
    categorizeTransactionsDomain: CategorizeTransactionsDomain
): ViewModel(), ICategorizeTransactionsDomain by categorizeTransactionsDomain