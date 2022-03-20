package com.tminus1010.budgetvalue.all_features.view.service

import android.content.Intent
import com.tminus1010.budgetvalue.all_features.view.HostActivity
import dagger.Reusable
import javax.inject.Inject

@Reusable
open class LaunchSelectFile @Inject constructor() {
    open operator fun invoke(hostActivity: HostActivity) {
        hostActivity.activityResultLauncher.launch(
            Intent().apply { type = "*/*"; action = Intent.ACTION_GET_CONTENT }
                .let { Intent.createChooser(it, "Select transactions csv") }
        )
    }
}