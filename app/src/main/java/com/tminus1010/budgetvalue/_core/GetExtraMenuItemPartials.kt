package com.tminus1010.budgetvalue._core

import com.tminus1010.budgetvalue._core.middleware.presentation.MenuVMItem
import javax.inject.Inject

open class GetExtraMenuItemPartials @Inject constructor() {
    open operator fun invoke() = emptyArray<MenuVMItem>()
}