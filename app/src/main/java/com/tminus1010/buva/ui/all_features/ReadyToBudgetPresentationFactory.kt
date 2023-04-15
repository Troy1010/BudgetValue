package com.tminus1010.buva.ui.all_features

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
    private val navigator: Navigator,
) {
    suspend fun tryShowAlertDialog(onContinue: () -> Unit) {
        runCatching { checkIfReady() }
            .onFailure {
                when (it) {
                    is CategorizationIsNotCompleteException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to complete categorization before budgeting.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onYes = { navigator.navToCategorize() },
                        )
                    is AccountsNotUpdatedRecentlyException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to update your accounts before budgeting.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onYes = { navigator.navToAccounts() },
                        )
                    is NoRecentTransactionImportItemException ->
                        activityWrapper.showAlertDialog(
                            body = NativeText.Simple("It's usually a good idea to import recent transactions before budgeting.\n\nDo you want to go there now?"),
                            onContinue = onContinue,
                            onYes = { navigator.navToImportTransactions() },
                        )
                }
            }
    }

    class CategorizationIsNotCompleteException : Exception()
    class AccountsNotUpdatedRecentlyException : Exception()
    class NoRecentTransactionImportItemException : Exception()

    // Check is in a separate exposed method so that you can block for it if you want.
    suspend fun checkIfReady() {
        // TODO: using .first() here might cause problems if default values before real emission would give incorrect results.
        if (!transactionsInteractor.transactionsAggregate.first().areAllSpendsCategorized)
            throw CategorizationIsNotCompleteException()
        else if (
            accountsRepo.accountsAggregate.first().accounts.isEmpty()
            || accountsRepo.accountsUpdateInfos.first().map { it.date }.maxByOrNull { it }
                ?.let { Period.between(it, LocalDate.now()).days > 7 } ?: true
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