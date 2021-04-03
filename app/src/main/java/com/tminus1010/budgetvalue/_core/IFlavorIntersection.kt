package com.tminus1010.budgetvalue._core

import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial
import com.tminus1010.budgetvalue._core.ui.HostActivity

interface IFlavorIntersection {
    fun getExtraMenuItemPartials(activity: HostActivity): Array<MenuItemPartial>
    fun launchImport(hostActivity: HostActivity)
}