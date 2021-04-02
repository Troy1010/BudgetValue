package com.tminus1010.budgetvalue._core

import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.testing.CustomTestApplication


// How to use CustomTestLauncher..?
// java.lang.RuntimeException: Unable to instantiate application com.tminus1010.budgetvalue._core.App_Development_Application: java.lang.RuntimeException: Hilt classes generated from @HiltAndroidTest are missing. Check that you have annotated your test class with @HiltAndroidTest and that the processor is running over your test
//@CustomTestApplication(BaseApp::class)
//interface App_Development