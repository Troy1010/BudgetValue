package com.tminus1010.budgetvalue.framework

import android.os.Looper

val isMainThread get() = Looper.myLooper() == Looper.getMainLooper()