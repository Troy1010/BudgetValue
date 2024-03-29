package com.tminus1010.buva.environment.android_wrapper

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ParcelableLambdaWrapper(val lambda: (String?) -> Unit) : Parcelable {
    fun lambda2(s: String?) = lambda(s)
}