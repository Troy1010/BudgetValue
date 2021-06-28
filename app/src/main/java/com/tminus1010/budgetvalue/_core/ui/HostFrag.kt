package com.tminus1010.budgetvalue._core.ui

import android.app.Application
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.ErrorVM
import com.tminus1010.budgetvalue._core.ImportFailedException
import com.tminus1010.budgetvalue._core.TestException
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonRVItem
import com.tminus1010.budgetvalue._core.extensions.getBackStack
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HostFrag : NavHostFragment() {
    @Inject lateinit var app: Application
    val errorVM by activityViewModels<ErrorVM>()
    fun getBackStack() = childFragmentManager.getBackStack()
    fun handle(e: Throwable, vararg buttonRVItems: ButtonRVItem) {
        val buttonPartialsRedef =
            listOf(
                ButtonRVItem("OK") { nav.navigateUp() }
            ) + buttonRVItems.toList()
        logz(e)
        when (e) {
            is ImportFailedException -> app.toast("Import failed")
            is TestException -> {
                errorVM.message.onNext("Test Exception")
                errorVM.buttons.onNext(buttonPartialsRedef.toList())
                logz("backstack1:${getBackStack()}")
                nav.navigate(R.id.errorFrag_clear_backstack)
                logz("backstack2:${getBackStack()}")
            }
            else -> {
                app.toast("An error occurred")
                nav.navigate(R.id.importFrag) // TODO("Clear backstack")
            }
        }
    }
}