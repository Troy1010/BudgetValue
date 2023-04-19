package com.tminus1010.buva.environment.android_wrapper

import android.os.Parcelable
import com.tminus1010.buva.domain.Transaction
import kotlinx.parcelize.Parcelize

@Parcelize
class ParcelableTransactionToBooleanLambdaWrapper(val lambda: (Transaction) -> Boolean) : Parcelable