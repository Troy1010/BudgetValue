package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.app.IsReadyToBudgeted
import com.tminus1010.buva.environment.ActivityWrapper
import com.tminus1010.tmcommonkotlin.view.NativeText
import javax.inject.Inject

class ReadyToBudgetedPresentationService @Inject constructor(
    private val activityWrapper: ActivityWrapper,
    private val navigator: Navigator,
    private val isReadyToBudgeted: IsReadyToBudgeted,
) {
    suspend fun tryShowAlertDialog(onContinue: () -> Unit) {
        runCatching { isReadyToBudgeted.check() }
            .onFailure {
                when (it) {
                    is IsReadyToBudgeted.ReconciliationRequiredException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to complete required reconciliations first.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onYes = { navigator.navToReconcile() },
                        )
                }
            }
    }
}