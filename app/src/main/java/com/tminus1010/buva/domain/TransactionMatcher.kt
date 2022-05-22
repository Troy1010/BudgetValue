package com.tminus1010.buva.domain

import android.os.Parcelable
import com.tminus1010.buva.all_layers.extensions.easyEquals
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

sealed class TransactionMatcher : Parcelable {
    abstract fun isMatch(transaction: Transaction): Boolean

    @Parcelize
    data class SearchText(val searchText: String) : TransactionMatcher() {
        override fun isMatch(transaction: Transaction): Boolean {
            return searchText.uppercase() in transaction.description.uppercase()
        }

        companion object {
            const val ordinal = 0
        }
    }

    @Parcelize
    data class ByValue(val searchTotal: BigDecimal) : TransactionMatcher() {
        override fun isMatch(transaction: Transaction): Boolean {
            return searchTotal.easyEquals(transaction.amount)
        }

        companion object {
            const val ordinal = 1
        }
    }

    @Parcelize
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

fun TransactionMatcher?.withSearchTotal(searchTotal: BigDecimal) =
    when (this) {
        null -> TransactionMatcher.ByValue(searchTotal)
        is TransactionMatcher.Multi -> TransactionMatcher.Multi(this.transactionMatchers.plus(TransactionMatcher.ByValue(searchTotal)))
        else -> TransactionMatcher.Multi(this, TransactionMatcher.ByValue(searchTotal))
    }

fun TransactionMatcher?.flattened() =
    when (this) {
        is TransactionMatcher.SearchText,
        is TransactionMatcher.ByValue,
        ->
            listOf(this)
        is TransactionMatcher.Multi ->
            this.transactionMatchers
        null ->
            listOf()
    }