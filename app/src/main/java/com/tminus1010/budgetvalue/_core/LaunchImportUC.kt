package com.tminus1010.budgetvalue._core

import android.content.Intent
import com.tminus1010.budgetvalue._core.flavor_contracts.development_production.ILaunchImportUC
import com.tminus1010.budgetvalue._core.ui.HostActivity
import javax.inject.Inject

class LaunchImportUC @Inject constructor() : ILaunchImportUC {
    override operator fun invoke(hostActivity: HostActivity) {
        Intent().apply { type = "*/*"; action = Intent.ACTION_GET_CONTENT }
            .let { Intent.createChooser(it, "Select transactions csv") }
            .also { hostActivity.activityResultLauncher.launch(it) }
    }
}