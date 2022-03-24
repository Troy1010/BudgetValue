package com.tminus1010.budgetvalue.ui.all_features

import android.content.Intent
import com.tminus1010.budgetvalue.ui.host.HostActivity
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