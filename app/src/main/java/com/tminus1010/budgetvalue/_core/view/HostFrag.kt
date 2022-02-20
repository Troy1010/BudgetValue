package com.tminus1010.budgetvalue._core.view

import android.app.Application
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue._core.ImportFailedException
import com.tminus1010.budgetvalue._core.TestException
import com.tminus1010.budgetvalue._core.all.extensions.getBackStack
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue._core.presentation.view_model.ErrorVM
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HostFrag : NavHostFragment() {
    @Inject
    lateinit var app: Application
    val errorVM by activityViewModels<ErrorVM>()
    fun handle(e: Throwable, vararg buttonVMItems: ButtonVMItem) {
        val buttonPartialsRedef =
            listOf(
                ButtonVMItem("OK") { nav.navigateUp() }
            ) + buttonVMItems.toList()
        logz(e)
        when (e) {
            is ImportFailedException -> app.easyToast("Import failed")
            is TestException -> {
                errorVM.message.onNext("Test Exception")
                errorVM.buttons.onNext(buttonPartialsRedef.toList())
                logz("backstack1:${childFragmentManager.getBackStack()}")
                nav.navigate(R.id.errorFrag_clear_backstack)
                logz("backstack2:${childFragmentManager.getBackStack()}")
            }
            else -> {
                app.easyToast("An error occurred")
                nav.navigate(R.id.importFrag) // TODO("Clear backstack")
            }
        }
    }
}