package com.tminus1010.budgetvalue

import android.app.Activity
import com.tminus1010.budgetvalue.layer_ui.HostActivity
import com.tminus1010.budgetvalue.layer_ui.misc.MenuItemPartial

interface IFlavorIntersection {
    fun getMenuItemPartials(activity: HostActivity): Array<MenuItemPartial>
    fun launchImport(activity: Activity)
}