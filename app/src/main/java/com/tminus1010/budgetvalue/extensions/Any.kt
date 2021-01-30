package com.tminus1010.budgetvalue.extensions

import com.tminus1010.tmcommonkotlin.logz.logz

fun <T> T.logzzz(msg:String?): T {
    return this.also { logz("$msg:$it") }
}