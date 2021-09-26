package com.tminus1010.budgetvalue.all.framework.extensions

import java.util.*

fun String.easyCapitalize(): String {
    return replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
}