package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.R
import com.tminus1010.buva.data.SelectedBudgetHostPage
import com.tminus1010.buva.data.SelectedHostPage
import com.tminus1010.buva.data.SelectedImportHostPage
import com.tminus1010.buva.environment.AndroidNavigationWrapper
import dagger.Reusable
import javax.inject.Inject

@Reusable
class Navigator @Inject constructor(
    private val androidNavigationWrapper: AndroidNavigationWrapper,
    private val selectedHostPage: SelectedHostPage,
    private val selectedImportHostPage: SelectedImportHostPage,
    private val selectedBudgetHostPage: SelectedBudgetHostPage,
) : AndroidNavigationWrapper by androidNavigationWrapper {

    fun navToAccounts() {
        selectedHostPage.set(R.id.importHostFrag)
        selectedImportHostPage.set(R.id.accountsFrag)
    }

    fun navToCategorize() {
        selectedHostPage.set(R.id.importHostFrag)
        selectedImportHostPage.set(R.id.categorizeNestedGraph)
    }

    fun navToImportTransactions() {
        selectedHostPage.set(R.id.importHostFrag)
        selectedImportHostPage.set(R.id.importTransactionsFrag)
    }
}