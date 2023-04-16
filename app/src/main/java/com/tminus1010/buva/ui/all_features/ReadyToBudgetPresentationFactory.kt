package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.app.IsReadyToBudget
import com.tminus1010.buva.environment.ActivityWrapper
import com.tminus1010.tmcommonkotlin.view.NativeText
import javax.inject.Inject

class ReadyToBudgetPresentationFactory @Inject constructor(
    private val isReadyToBudget: IsReadyToBudget,
    private val activityWrapper: ActivityWrapper,
    private val navigator: Navigator,
) {
    suspend fun tryShowAlertDialog(onContinue: () -> Unit) {
        runCatching { isReadyToBudget.check() }
            .onFailure {
                when (it) {
                    is IsReadyToBudget.CategorizationIsNotCompleteException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to complete categorization before budgeting.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onYes = { navigator.navToCategorize() },
                        )
                    is IsReadyToBudget.AccountsNotUpdatedRecentlyException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to update your accounts before budgeting.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onYes = { navigator.navToAccounts() },
                        )
                    is IsReadyToBudget.NoRecentTransactionImportItemException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to import recent transactions before budgeting.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onYes = { navigator.navToImportTransactions() },
                        )
                }
            }
    }
}