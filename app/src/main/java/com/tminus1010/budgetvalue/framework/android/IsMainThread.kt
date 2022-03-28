package com.tminus1010.budgetvalue.framework

import android.os.Handler
import android.os.Looper

val isMainThread get() = Looper.myLooper() == Looper.getMainLooper()

fun launchOnMainThread(lambda: () -> Unit) {
    if (isMainThread)
        lambda()
    else
        Handler(Looper.getMainLooper()).post(lambda)
}