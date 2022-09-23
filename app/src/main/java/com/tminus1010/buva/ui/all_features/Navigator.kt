package com.tminus1010.buva.ui.all_features

import dagger.Reusable
import javax.inject.Inject

@Reusable
class Navigator @Inject constructor(
    private val androidNavigationWrapper: AndroidNavigationWrapper,
) : IAndroidNavigationWrapper by androidNavigationWrapper