package com.tminus1010.budgetvalue.extensions

import androidx.fragment.app.FragmentManager

fun FragmentManager.getBackStack() = (0 until backStackEntryCount).map { getBackStackEntryAt(it) }