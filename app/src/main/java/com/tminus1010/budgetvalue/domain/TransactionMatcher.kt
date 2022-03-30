package com.tminus1010.budgetvalue.domain

import com.tminus1010.budgetvalue.all_layers.extensions.easyEquals
import java.math.BigDecimal

sealed class TransactionMatcher {
    abstract fun isMatch(transaction: Transaction): Boolean
    data class SearchText(val searchText: String) : TransactionMatcher() {
        override fun isMatch(transaction: Transaction): Boolean {
            return searchText.uppercase() in transaction.description.uppercase()
        }

        companion object {
            const val ordinal = 0
        }
    }

    data class ByValue(val searchTotal: BigDecimal) : TransactionMatcher() {
        override fun isMatch(transaction: Transaction): Boolean {
            return searchTotal.easyEquals(transaction.amount)
        }

        companion object {
            const val ordinal = 1
        }
    }

    data class Multi(val transactionMatchers: List<TransactionMatcher>) : TransactionMatcher() {
        constructor(vararg transactionMatchers: TransactionMatcher?) : this(transactionMatchers.toList().filterNotNull())

        override fun isMatch(transaction: Transaction): Boolean {
            return transactionMatchers.any { it.isMatch(transaction) }
        }

        companion object {
            const val ordinal = 2
        }
    }
}

fun TransactionMatcher?.withSearchText(searchText: String) =
    when (this) {
        null -> TransactionMatcher.SearchText(searchText)
        is TransactionMatcher.Multi -> TransactionMatcher.Multi(this.transactionMatchers.plus(TransactionMatcher.SearchText(searchText)))
        else -> TransactionMatcher.Multi(this, TransactionMatcher.SearchText(searchText))
    }