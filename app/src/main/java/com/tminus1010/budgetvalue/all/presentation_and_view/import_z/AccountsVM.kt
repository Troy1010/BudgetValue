package com.tminus1010.budgetvalue.all.presentation_and_view.import_z

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.middleware.presentation.ButtonVMItem
import com.tminus1010.budgetvalue.all.data.repos.AccountsRepo
import com.tminus1010.budgetvalue.all.domain.models.Account
import com.tminus1010.budgetvalue.all.presentation_and_view._view_model_item.AccountVMItem
import com.tminus1010.budgetvalue.all.presentation_and_view._view_model_item.AccountsVMItem
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
    // # Presentation Output
    val navToSelectFile = PublishSubject.create<Unit>()!!
    val accounts =
        accountsRepo.accounts
            .map { it.accounts.map { AccountVMItem(it, accountsRepo) } }
            .replayNonError(1)
            .nonLazy(disposables)
    val accountsTotal =
        accountsRepo.accounts
            .map(::AccountsVMItem)
            .map(AccountsVMItem::total)
            .replayNonError(1)
            .nonLazy(disposables)
    val buttons =
        listOfNotNull(
            ButtonVMItem(
                title = "Import",
                onClick = { navToSelectFile.onNext(Unit) }
            ),
            ButtonVMItem(
                title = "Add Account",
                onClick = { accountsRepo.add(Account("", BigDecimal.ZERO)).subscribe() }
            ),
        )
}