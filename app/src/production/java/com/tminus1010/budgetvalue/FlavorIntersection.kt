package com.tminus1010.budgetvalue

import android.content.Intent
import com.tminus1010.budgetvalue.aa_core.IFlavorIntersection
import com.tminus1010.budgetvalue.aa_core.middleware.ui.MenuItemPartial
import com.tminus1010.budgetvalue.aa_shared.ui.HostActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlavorIntersection @Inject constructor() : IFlavorIntersection {
    override fun getExtraMenuItemPartials(activity: HostActivity): Array<MenuItemPartial> =
        emptyArray()

    override fun launchImport(hostActivity: HostActivity) {
        Intent().apply { type = "*/*"; action = Intent.ACTION_GET_CONTENT }
            .let { Intent.createChooser(it, "Select transactions csv") }
            .also { hostActivity.activityResultLauncher.launch(it) }
    }
}