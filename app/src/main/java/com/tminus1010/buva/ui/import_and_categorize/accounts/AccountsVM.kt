package com.tminus1010.buva.ui.import_and_categorize.importZ

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tminus1010.buva.data.AccountsRepo
import com.tminus1010.buva.domain.Account
import com.tminus1010.buva.ui.all_features.view_model_item.AccountsPresentationModel
import com.tminus1010.buva.ui.all_features.view_model_item.ButtonVMItem
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