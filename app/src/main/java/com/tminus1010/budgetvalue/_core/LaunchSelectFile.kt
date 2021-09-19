package com.tminus1010.budgetvalue._core

import android.content.Intent
import com.tminus1010.budgetvalue._core.presentation_and_view.host.HostActivity
import javax.inject.Inject

open class LaunchSelectFile @Inject constructor() {
    open operator fun invoke(hostActivity: HostActivity) {
        Intent().apply { type = "*/*"; action = Intent.ACTION_GET_CONTENT }
            .let { Intent.createChooser(it, "Select transactions csv") }
            .also { hostActivity.activityResultLauncher.launch(it) }
    }
}