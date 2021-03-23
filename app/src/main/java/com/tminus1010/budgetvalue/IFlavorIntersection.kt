package com.tminus1010.budgetvalue

import com.tminus1010.budgetvalue.layer_ui.HostActivity
import com.tminus1010.budgetvalue.middleware.ui.MenuItemPartial

interface IFlavorIntersection {
    fun getExtraMenuItemPartials(activity: HostActivity): Array<MenuItemPartial>
    fun launchImport(hostActivity: HostActivity)
}