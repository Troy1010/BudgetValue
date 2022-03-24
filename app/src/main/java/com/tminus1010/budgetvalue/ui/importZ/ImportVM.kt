package com.tminus1010.budgetvalue.ui.importZ

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.all_layers.extensions.invoke
import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.domain.accounts.Account
import com.tminus1010.budgetvalue.ui.all_features.model.AccountsPresentationModel
import com.tminus1010.budgetvalue.ui.all_features.model.ButtonVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ImportVM @Inject constructor(
    private val accountsRepo: AccountsRepo,
) : ViewModel() {
    // # User Intents
    fun userAddAccount() {
        GlobalScope.launch { accountsRepo.add(Account("", BigDecimal.ZERO)) }
    }

    // # Events
    val navToSelectFile = PublishSubject.create<Unit>()

    // # State
    val accountVMItemList =
        accountsRepo.accountsAggregate
            .map { AccountsPresentationModel(it, accountsRepo) }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(
                    title = "Import",
                    onClick = navToSelectFile::invoke
                ),
                ButtonVMItem(
                    title = "Add Account",
                    onClick = { userAddAccount() }
                ),
            )
        )
}