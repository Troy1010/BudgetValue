package com.tminus1010.buva.environment

import android.os.Parcelable
import com.tminus1010.buva.domain.Transaction
import kotlinx.parcelize.Parcelize

@Parcelize
class ParcelableTransactionLambdaWrapper(val lambda: (Transaction?) -> Unit) : Parcelable {
    fun lambda2(transaction: Transaction?) = lambda(transaction)
}