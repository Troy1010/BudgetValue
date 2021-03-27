package com.tminus1010.budgetvalue.aa_core

import com.tminus1010.budgetvalue.aa_core.middleware.ui.MenuItemPartial
import com.tminus1010.budgetvalue.aa_shared.ui.HostActivity

interface IFlavorIntersection {
    fun getExtraMenuItemPartials(activity: HostActivity): Array<MenuItemPartial>
    fun launchImport(hostActivity: HostActivity)
}