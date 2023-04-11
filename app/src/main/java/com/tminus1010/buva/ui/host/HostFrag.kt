package com.tminus1010.buva.ui.host

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.ImportFailedException
import com.tminus1010.buva.all_layers.TestException
import com.tminus1010.buva.all_layers.extensions.getBackStack
import com.tminus1010.buva.ui.all_features.Navigator
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.buva.ui.errors.ErrorVM
import com.tminus1010.tmcommonkotlin.androidx.ShowToast
import com.tminus1010.tmcommonkotlin.androidx.launchOnMainThread
import com.tminus1010.tmcommonkotlin.view.NativeText
import com.tminus1010.tmcommonkotlin.view.extensions.nav
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HostFrag : NavHostFragment() {
    @Inject
    lateinit var showToast: ShowToast

    @Inject
    lateinit var navigator: Navigator
    val errorVM by activityViewModels<ErrorVM>()
    fun handle(e: Throwable, vararg buttonVMItems: ButtonVMItem) {
        val buttonPartialsRedef =
            listOf(
                ButtonVMItem("OK") { nav.navigateUp() }
            ) + buttonVMItems.toList()
        logz(e)
        launchOnMainThread {
            when (e) {
                is ImportFailedException -> showToast(NativeText.Simple("Import failed"))
                is TestException -> {
                    errorVM.message.onNext("Test Exception")
                    errorVM.buttons.onNext(buttonPartialsRedef.toList())
                    logz("backstack1:${childFragmentManager.getBackStack()}")
                    nav.navigate(R.id.errorFrag_clear_backstack)
                    logz("backstack2:${childFragmentManager.getBackStack()}")
                }
                else -> {
                    showToast(NativeText.Simple("An error occurred"))
                    navigator.navToImportTransactions()
                }
            }
        }
    }
}