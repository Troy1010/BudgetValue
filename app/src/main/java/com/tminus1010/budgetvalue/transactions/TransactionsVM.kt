package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._layer_facades.DomainFacade
import com.tminus1010.budgetvalue.categories.Category
import com.tminus1010.tmcommonkotlin.rx.extensions.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.InputStream
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TransactionsVM @Inject constructor(
    transactionsDomain: TransactionsDomain
) : ViewModel(), ITransactionsDomain by transactionsDomain