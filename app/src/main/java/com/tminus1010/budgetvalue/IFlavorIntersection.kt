package com.tminus1010.budgetvalue

import com.tminus1010.budgetvalue.layer_ui.HostActivity
import com.tminus1010.budgetvalue.layer_ui.misc.MenuItemPartial

interface IFlavorIntersection {
    fun getExtraMenuItemPartials(activity: HostActivity): Array<MenuItemPartial>
    fun launchImport(hostActivity: HostActivity)
}