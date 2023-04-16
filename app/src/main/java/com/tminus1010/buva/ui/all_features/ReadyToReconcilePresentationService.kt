package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.extensions.isZero
import com.tminus1010.buva.app.IsReadyToReconcile
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.data.SelectedBudgetHostPage
import com.tminus1010.buva.domain.ReconciliationStrategyGroup
import com.tminus1010.buva.domain.ResetStrategy
import com.tminus1010.buva.environment.ActivityWrapper
import com.tminus1010.tmcommonkotlin.view.NativeText
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ReadyToReconcilePresentationService @Inject constructor(
    private val activityWrapper: ActivityWrapper,
    private val selectedBudgetHostPage: SelectedBudgetHostPage,
    private val isReadyToReconcile: IsReadyToReconcile,
) {
    suspend fun tryShowAlertDialog() {
        runCatching { isReadyToReconcile.check() }
            .onFailure {
                when (it) {
                    is IsReadyToReconcile.PlanIsInvalidException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to add max values to reservoir categories before reconciling.\n\nDo you want to go there now?"),
                            onContinue = { selectedBudgetHostPage.set(R.id.reconciliationHostFrag) },
                            onYes = { selectedBudgetHostPage.set(R.id.planFrag) },
                        )
                }
            }
    }
}