package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.R
import com.tminus1010.buva.data.SelectedHostPage
import com.tminus1010.buva.environment.AndroidNavigationWrapper
import dagger.Reusable
import javax.inject.Inject

@Reusable
class Navigator @Inject constructor(
    private val androidNavigationWrapper: AndroidNavigationWrapper,
    private val selectedHostPage: SelectedHostPage,
) : AndroidNavigationWrapper by androidNavigationWrapper {

    override fun navToAccounts() {
        selectedHostPage.set(R.id.importHostFrag)
        androidNavigationWrapper.navToAccounts()
    }
}