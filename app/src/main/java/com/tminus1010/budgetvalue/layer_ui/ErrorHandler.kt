package com.tminus1010.budgetvalue.layer_ui

import androidx.navigation.NavController
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.ImportFailedException
import com.tminus1010.budgetvalue.R
import com.tminus1010.tmcommonkotlin.misc.logz
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor(private val app: App) {
    fun handle(navController: NavController, e: Throwable) {
        logz(e)
        when (e) {
            is ImportFailedException -> app.toast("Import failed")
            else -> {
                app.toast("An error occurred")
                navController.navigate(R.id.importFrag)
            }
        }
    }
}