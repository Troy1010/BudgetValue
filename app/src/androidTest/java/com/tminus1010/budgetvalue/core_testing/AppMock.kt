package com.tminus1010.budgetvalue.core_testing

import com.tminus1010.budgetvalue.all_layers.BaseApp
import dagger.hilt.android.testing.CustomTestApplication

@CustomTestApplication(BaseApp::class)
interface AppMock