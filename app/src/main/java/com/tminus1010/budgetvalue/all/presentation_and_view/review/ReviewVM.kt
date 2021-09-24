package com.tminus1010.budgetvalue.all.presentation_and_view.review

import androidx.lifecycle.ViewModel
import com.tminus1010.budgetvalue._core.models.CategoryAmounts
import com.tminus1010.budgetvalue.all.presentation_and_view.services.createPieData
import com.tminus1010.budgetvalue.transactions.data.TransactionsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReviewVM @Inject constructor(
    transactionsRepo: TransactionsRepo
) : ViewModel() {
    val pieData =
        transactionsRepo.transactions
            .map { it.fold(CategoryAmounts()) { acc, transaction -> acc.addTogether(transaction.categoryAmounts) } }
            .map(::createPieData)!!
}