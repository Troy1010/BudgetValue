package com.tminus1010.budgetvalue._core.all.extensions


fun <T> T?.ifNull(lambda: () -> T?): T? {
    return this ?: lambda()
}