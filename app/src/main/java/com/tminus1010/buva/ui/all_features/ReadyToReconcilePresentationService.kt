package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.app.IsReadyToReconcile
import com.tminus1010.buva.environment.ActivityWrapper
import com.tminus1010.tmcommonkotlin.view.NativeText
import javax.inject.Inject

class ReadyToReconcilePresentationService @Inject constructor(
    private val activityWrapper: ActivityWrapper,
    private val navigator: Navigator,
    private val isReadyToReconcile: IsReadyToReconcile,
) {
    suspend fun tryShowAlertDialog(onContinue: () -> Unit) {
        runCatching { isReadyToReconcile.check() }
            .onFailure {
                when (it) {
                    is IsReadyToReconcile.PlanIsInvalidException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to add max values to reservoir categories first.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onYes = { navigator.navToPlan() },
                        )
                }
            }
    }
}