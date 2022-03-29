package com.tminus1010.budgetvalue.ui.importZ

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.budgetvalue.data.AccountsRepo
import com.tminus1010.budgetvalue.domain.accounts.Account
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.AccountsPresentationModel
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.ButtonVMItem
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val importSharedVM: ImportSharedVM,
) : ViewModel() {
    // # User Intents
    fun userAddAccount() {
        GlobalScope.launch { accountsRepo.add(Account("", BigDecimal.ZERO)) }
    }

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
                    onClick = importSharedVM::userTryNavToSelectFile
                ),
                ButtonVMItem(
                    title = "Add Account",
                    onClick = ::userAddAccount
                ),
            )
        )
}