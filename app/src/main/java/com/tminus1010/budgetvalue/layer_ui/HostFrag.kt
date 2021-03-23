package com.tminus1010.budgetvalue.layer_ui

import androidx.navigation.fragment.NavHostFragment
import com.tminus1010.budgetvalue.ImportFailedException
import com.tminus1010.budgetvalue.R
import com.tminus1010.budgetvalue.TestException
import com.tminus1010.budgetvalue.dependency_injection.ViewModelProviders
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.app
import com.tminus1010.budgetvalue.dependency_injection.injection_extensions.appComponent
import com.tminus1010.budgetvalue.extensions.getBackStack
import com.tminus1010.budgetvalue.middleware.ui.ButtonPartial
import com.tminus1010.budgetvalue.features_shared.IViewModels
import com.tminus1010.tmcommonkotlin.misc.logz
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import com.tminus1010.tmcommonkotlin.view.extensions.toast

class HostFrag : NavHostFragment(), IViewModels {
    override val viewModelProviders by lazy { ViewModelProviders(requireActivity(), appComponent) }
    fun getBackStack() = childFragmentManager.getBackStack()
    fun handle(e: Throwable, vararg buttonPartials: ButtonPartial) {
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