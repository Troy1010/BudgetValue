package com.tminus1010.budgetvalue.ui.host

import android.content.Intent
import dagger.Reusable
import javax.inject.Inject

@Reusable
open class LaunchChooseFile @Inject constructor() {
    open operator fun invoke(hostActivity: HostActivity) {
        hostActivity.importTransactionsLauncher.launch(
            Intent().apply { type = "*/*"; action = Intent.ACTION_GET_CONTENT }
                .let { Intent.createChooser(it, "Select transactions csv") }
        )
    }
}