package com.tminus1010.budgetvalue._core.middleware

fun logt(prefix: Any? = "") {
    val prefixRedef = if (prefix != "") "$prefix`" else prefix
    logz("${prefixRedef}Current Thread:${Thread.currentThread().name}")
}