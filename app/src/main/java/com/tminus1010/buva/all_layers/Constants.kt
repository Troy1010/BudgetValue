package com.tminus1010.buva.all_layers

import com.tminus1010.buva.domain.Category

// * TODO("Give this to a VM, and allow the user to change it.")
val categoryComparator = compareBy<Category>({ category -> category.type }, { category -> category.name })

const val KEY1 = "KEY1"
const val KEY2 = "KEY2"
const val KEY3 = "KEY3"
const val KEY4 = "KEY4"
const val KEY5 = "KEY5"