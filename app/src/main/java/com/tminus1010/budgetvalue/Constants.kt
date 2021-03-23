package com.tminus1010.budgetvalue

import com.tminus1010.budgetvalue.features.categories.Category

const val SHARED_PREF_FILE_NAME = "SharedPref"
// * TODO("Give this to a VM, and allow the user to change it.")
val categoryComparator = compareBy<Category>({ category -> category.type }, { category -> category.name })