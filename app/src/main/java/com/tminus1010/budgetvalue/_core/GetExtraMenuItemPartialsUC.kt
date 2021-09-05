package com.tminus1010.budgetvalue._core

import com.tminus1010.budgetvalue._core.middleware.ui.MenuVMItem
import com.tminus1010.budgetvalue._core.ui.HostActivity
import javax.inject.Inject

open class GetExtraMenuItemPartialsUC @Inject constructor() {
    open operator fun invoke(hostActivity: HostActivity) = emptyArray<MenuVMItem>()
}