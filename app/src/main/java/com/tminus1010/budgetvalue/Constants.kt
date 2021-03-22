package com.tminus1010.budgetvalue

import com.tminus1010.budgetvalue.modules.categories.Category

const val CODE_PICK_TRANSACTIONS_FILE = 3486
const val SHARED_PREF_FILE_NAME = "SharedPref"
// * TODO("Give this to a VM, and allow the user to change it.")
val categoryComparator = compareBy<Category>({ category -> category.type }, { category -> category.name })