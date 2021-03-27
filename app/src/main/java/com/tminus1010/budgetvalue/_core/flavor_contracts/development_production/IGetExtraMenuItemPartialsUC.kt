package com.tminus1010.budgetvalue._core.flavor_contracts.development_production

import com.tminus1010.budgetvalue._core.middleware.ui.MenuItemPartial
import com.tminus1010.budgetvalue._core.ui.HostActivity

interface IGetExtraMenuItemPartialsUC {
    operator fun invoke(hostActivity: HostActivity): Array<MenuItemPartial>
}