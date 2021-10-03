package com.tminus1010.budgetvalue.accounts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.accounts.data.AccountsRepo
import com.tminus1010.budgetvalue.accounts.app.Account
import com.tminus1010.budgetvalue.all.framework.extensions.invoke
import com.tminus1010.tmcommonkotlin.rx.nonLazy
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountsVM @Inject constructor(
    private val accountsRepo: AccountsRepo,
) : ViewModel() {
    // # Events
    val navToSelectFile = PublishSubject.create<Unit>()

    // # Presentation State
    val accountVMItemList =
        accountsRepo.accountsAggregate
            .map { AccountVMItemList(it, accountsRepo) }
            .replayNonError(1)
            .nonLazy(disposables)
    val buttons =
        listOfNotNull(
            ButtonVMItem(
                title = "Import",
                userClick = navToSelectFile::invoke
            ),
            ButtonVMItem(
                title = "Add Account",
                userClick = { accountsRepo.add(Account("", BigDecimal.ZERO)).subscribe() }
            ),
        )
}