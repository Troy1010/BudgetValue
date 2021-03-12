package com.tminus1010.budgetvalue.layer_ui

import androidx.navigation.NavController
import com.tminus1010.budgetvalue.App
import com.tminus1010.budgetvalue.ImportFailedException
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.TestException
import com.tminus1010.budgetvalue.layer_ui.misc.ButtonPartial
import com.tminus1010.tmcommonkotlin.misc.logz
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor(
    private val app: App,
    private val errorVM: ErrorVM,
) {
    fun handle(nav: NavController, e: Throwable, vararg buttonPartials: ButtonPartial) {
        val buttonPartialsRedef =
            listOf(
                ButtonPartial("OK") { nav.navigateUp() }
            ) + buttonPartials.toList()
        logz(e)
        when (e) {
            is ImportFailedException -> app.toast("Import failed")
            is TestException -> {
                errorVM.message.onNext("Test Exception")
                errorVM.buttons.onNext(buttonPartialsRedef.toList())
                nav.navigate(R.id.errorFrag_clear_backstack)
            }
            else -> {
                app.toast("An error occurred")
                nav.navigate(R.id.importFrag)
            }
        }
    }
}