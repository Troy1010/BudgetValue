package com.tminus1010.budgetvalue

import androidx.test.core.app.ApplicationProvider
import com.tminus1010.budgetvalue.dependency_injection.DaggerAppComponent
import com.tminus1010.budgetvalue.dependency_injection.MiscModule
import io.mockk.mockk

/**
 * Will not work without something like:
 *  {@code
 *      @RunWith(RobolectricTestRunner::class)
 *      @Config(application = App::class)
 *      class TheTest () {
 *  }
 */
val appComponent by lazy { ApplicationProvider.getApplicationContext<App>().appComponent }

/**
 * Does not require robolectric and runs faster,
 * but does not have a mocked app.
 */
val appComponent2 by lazy {
    DaggerAppComponent.builder()
        .miscModule(MiscModule(mockk()))
        .build()
}