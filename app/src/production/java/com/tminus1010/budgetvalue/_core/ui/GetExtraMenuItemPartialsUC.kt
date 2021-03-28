package com.tminus1010.budgetvalue._core.ui

import com.tminus1010.budgetvalue._core.flavor_contracts.development_production.IGetExtraMenuItemPartialsUC
import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial
import javax.inject.Inject

class GetExtraMenuItemPartialsUC @Inject constructor() : IGetExtraMenuItemPartialsUC {
    override operator fun invoke(hostActivity: HostActivity) = emptyArray<MenuItemPartial>()
}