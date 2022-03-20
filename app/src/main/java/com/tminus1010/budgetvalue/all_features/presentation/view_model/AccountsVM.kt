package com.tminus1010.budgetvalue.all_features.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue.all_features.presentation.model.AccountsPresentationModel
import com.tminus1010.budgetvalue.all_features.presentation.model.ButtonVMItem
import com.tminus1010.budgetvalue.all_features.data.repo.AccountsRepo
import com.tminus1010.budgetvalue.all_features.domain.accounts.Account
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.invoke
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

    // # State
    val accountVMItemList =
        accountsRepo.accountsAggregate
            .map { AccountsPresentationModel(it, accountsRepo) }
            .replayNonError(1)
            .nonLazy(disposables)
    val buttons =
        listOfNotNull(
            ButtonVMItem(
                title = "Import",
                onClick = navToSelectFile::invoke
            ),
            ButtonVMItem(
                title = "Add Account",
                onClick = { accountsRepo.add(Account("", BigDecimal.ZERO)).subscribe() }
            ),
        )
}