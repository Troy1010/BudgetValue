package com.tminus1010.budgetvalue

import android.app.Activity
import android.content.Intent
import com.tminus1010.budgetvalue.layer_ui.HostActivity
import com.tminus1010.budgetvalue.layer_ui.misc.MenuItemPartial
import javax.inject.Inject

class FlavorIntersection @Inject constructor(): IFlavorIntersection {
    override fun getMenuItemPartials(activity: HostActivity): Array<MenuItemPartial> {
        return emptyArray()
    }

    override fun launchImport(activity: Activity) {
        Intent().apply { type = "*/*"; action = Intent.ACTION_GET_CONTENT }.also {
            activity.startActivityForResult(
                Intent.createChooser(it, "Select transactions csv"),
                CODE_PICK_TRANSACTIONS_FILE
            )
        }
    }
}