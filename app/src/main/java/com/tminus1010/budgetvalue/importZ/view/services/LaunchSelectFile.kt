package com.tminus1010.budgetvalue.importZ.view.services

import android.content.Intent
import com.tminus1010.budgetvalue._core.view.HostActivity
import javax.inject.Inject

open class LaunchSelectFile @Inject constructor() {
    open operator fun invoke(hostActivity: HostActivity) {
        hostActivity.activityResultLauncher.launch(
            Intent().apply { type = "*/*"; action = Intent.ACTION_GET_CONTENT }
                .let { Intent.createChooser(it, "Select transactions csv") }
        )
    }
}