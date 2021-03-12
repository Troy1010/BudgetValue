package com.tminus1010.budgetvalue.layer_ui

import androidx.navigation.NavController
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.ImportFailedException
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.TestException
import com.tminus1010.tmcommonkotlin.misc.logz
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor(private val app: App) {
    fun handle(nav: NavController, e: Throwable) {
        logz(e)
        when (e) {
            is ImportFailedException -> app.toast("Import failed")
            is TestException -> nav.navigate(R.id.errorFrag)
            else -> {
                app.toast("An error occurred")
                nav.navigate(R.id.importFrag)
            }
        }
    }
}