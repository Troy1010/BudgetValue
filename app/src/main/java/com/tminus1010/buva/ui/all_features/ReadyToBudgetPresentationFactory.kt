package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.all_layers.extensions.value
import com.tminus1010.buva.app.TransactionsInteractor
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.data.TransactionsRepo
import com.tminus1010.buva.environment.ActivityWrapper
import com.tminus1010.tmcommonkotlin.view.NativeText
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.Period
import javax.inject.Inject

class ReadyToBudgetPresentationFactory @Inject constructor(
    private val transactionsInteractor: TransactionsInteractor,
    private val accountsRepo: AccountsRepo,
    private val transactionsRepo: TransactionsRepo,
    private val activityWrapper: ActivityWrapper,
) {
    suspend fun tryShowAlertDialog(onContinue: () -> Unit) {
        runCatching { checkIfReadyToBudget() }
            .onFailure {
                when (it) {
                    is CategorizationIsNotCompleteException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's not recommended to reconcile until after categorization is complete.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onRedirect = { TODO() },
                        )
                    is NoAccountsExistException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's not recommended to reconcile until after an account has been added.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onRedirect = { TODO() },
                        )
                    is AccountsNotUpdatedRecentlyException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's not recommended to reconcile if accounts have not been updated recently.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onRedirect = { TODO() },
                        )
                    is NoRecentTransactionImportItemException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's not recommended to reconcile if transactions have not been imported recently.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onRedirect = { TODO() },
                        )
                }
            }
    }

    class CategorizationIsNotCompleteException : Exception()
    class NoAccountsExistException : Exception()
    class AccountsNotUpdatedRecentlyException : Exception()
    class NoRecentTransactionImportItemException : Exception()

    // Check is in a separate exposed method so that you can block for it if you want.
    suspend fun checkIfReadyToBudget() {
        if (!transactionsInteractor.transactionsAggregate.first().areAllSpendsCategorized)
            throw CategorizationIsNotCompleteException()
        else if (accountsRepo.accountsAggregate.value?.accounts?.ifEmpty { null } == null)
            throw NoAccountsExistException()
        else if (
            accountsRepo.accountsUpdateInfos.first().map { it.date }.maxByOrNull { it }
                ?.let { Period.between(it, LocalDate.now()).days > 7 }
                ?: true
        )
            throw AccountsNotUpdatedRecentlyException()
        else if (
            transactionsRepo.mostRecentImportItemDate.first()
                ?.let { Period.between(it, LocalDate.now()).days > 7 }
                ?: true
        )
            throw NoRecentTransactionImportItemException()
    }
}