package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.app.IsReadyToBudget
import com.tminus1010.buva.environment.ActivityWrapper
import com.tminus1010.tmcommonkotlin.view.NativeText
import javax.inject.Inject

class ReadyToBudgetPresentationService @Inject constructor(
    private val activityWrapper: ActivityWrapper,
    private val navigator: Navigator,
    private val isReadyToBudget: IsReadyToBudget,
) {
    suspend fun tryShowAlertDialog(onContinue: () -> Unit) {
        runCatching { isReadyToBudget.check() }
            .onFailure {
                when (it) {
                    is IsReadyToBudget.CategorizationIsNotCompleteException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to complete categorization first.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onYes = { navigator.navToCategorize() },
                        )
                    is IsReadyToBudget.AccountsNotUpdatedRecentlyException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to update your accounts first.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onYes = { navigator.navToAccounts() },
                        )
                    is IsReadyToBudget.NoRecentTransactionImportItemException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to import recent transactions first.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onYes = { navigator.navToImportTransactions() },
                        )
                }
            }
    }
}