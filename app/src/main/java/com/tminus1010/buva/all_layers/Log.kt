package com.tminus1010.buva.all_layers

import android.util.Log
import com.tminus1010.tmcommonkotlin.core.isNonInstrumentationTest

fun logq(msg: Any?) {
    Log.d("LOGQ", "LQ`$msg")
}

inline fun <reified T> T.logc(prefix: Any? = null, lambda: (x: T) -> Any?): T {
    return this.apply {
        val prefixLogStr = prefix?.let { "$prefix`" } ?: ""
        if (isNonInstrumentationTest)
            when (this) {
                is Throwable -> println("TM`Error:${this.message}")
                else -> println("TM`${prefixLogStr}${lambda(this)}")
            }
        else
            when (this) {
                is Throwable -> Log.e("TMLog", "TM`${prefixLogStr}Error:", this)
                else -> Log.d("TMLog", "TM`${prefixLogStr}${lambda(this)}")
            }
    }
}