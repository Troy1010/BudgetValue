package com.tminus1010.buva.ui.all_features

import com.tminus1010.buva.environment.AndroidNavigationWrapper
import dagger.Reusable
import javax.inject.Inject

@Reusable
class Navigator @Inject constructor(
    private val androidNavigationWrapper: AndroidNavigationWrapper,
) : AndroidNavigationWrapper by androidNavigationWrapper {

}