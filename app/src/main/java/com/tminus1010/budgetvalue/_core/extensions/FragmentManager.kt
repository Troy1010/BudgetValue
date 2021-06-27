package com.tminus1010.budgetvalue._core.extensions

import androidx.fragment.app.FragmentManager

fun FragmentManager.getBackStack() = (0 until backStackEntryCount).map { getBackStackEntryAt(it) }