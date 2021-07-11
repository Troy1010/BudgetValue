package com.tminus1010.budgetvalue._core.extensions

import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.uncheckAllItems() {
    menu.setGroupCheckable(0, true, false)
    (0 until menu.size()).forEach { menu.getItem(it).isChecked = false }
    menu.setGroupCheckable(0, true, true)
}