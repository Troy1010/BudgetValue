package com.tminus1010.budgetvalue.test

import android.content.res.AssetManager
import androidx.test.platform.app.InstrumentationRegistry

/**
 * I could not figure out how to launch an Activity in androidTest/, but.. this is a way so at least the assets can be defined in androidTest/
 */
class AndroidTestAssets {
    val assets: AssetManager = InstrumentationRegistry.getInstrumentation().context.assets
}