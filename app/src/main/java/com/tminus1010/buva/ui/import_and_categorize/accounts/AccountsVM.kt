package com.tminus1010.buva.ui.import_and_categorize.transactions

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.domain.Account
import com.tminus1010.buva.ui.all_features.view_model_item.AccountsPresentationModel
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
import com.tminus1010.tmcommonkotlin.core.extensions.toDisplayStr
import com.tminus1010.tmcommonkotlin.view.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class AccountsVM @Inject constructor(
    private val accountsRepo: AccountsRepo,
) : ViewModel() {
    // # User Intents
    fun userAddAccount() {
        GlobalScope.launch { accountsRepo.add(Account("", BigDecimal.ZERO)) }
    }

    // # State
    val noAccountsTextVisibility =
        accountsRepo.accountsAggregate
            .map { if (it.accounts.isNotEmpty()) View.GONE else View.VISIBLE }
    val accountsRecentlyUpdated =
        combine(accountsRepo.accountsUpdateInfos, noAccountsTextVisibility)
        { accountsUpdateInfos, noAccountsTextVisibility ->
            val mostRecentUpdateDate = accountsUpdateInfos.map { it.date }.maxByOrNull { it }
            if (mostRecentUpdateDate == null || noAccountsTextVisibility == View.VISIBLE)
                null
            else
                NativeText.Simple("Accounts were most recently updated on: ${mostRecentUpdateDate.toDisplayStr()}")
        }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val accountsRecentlyUpdatedVisibility =
        accountsRecentlyUpdated.map { if (it == null) View.GONE else View.VISIBLE }
    val accountVMItemList =
        accountsRepo.accountsAggregate
            .map { AccountsPresentationModel(it, accountsRepo) }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
    val buttons =
        flowOf(
            listOfNotNull(
                ButtonVMItem(
                    title = "Add Account",
                    onClick = ::userAddAccount
                ),
            )
        )
}