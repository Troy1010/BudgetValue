package com.tminus1010.budgetvalue._core

import com.tminus1010.budgetvalue._core.middleware.presentation.MenuVMItem
import com.tminus1010.budgetvalue._core.ui.HostActivity
import javax.inject.Inject

open class GetExtraMenuItemPartials @Inject constructor() {
    open operator fun invoke(hostActivity: HostActivity) = emptyArray<MenuVMItem>()
}