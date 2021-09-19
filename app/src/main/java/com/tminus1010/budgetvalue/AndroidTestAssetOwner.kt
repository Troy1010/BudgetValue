package com.tminus1010.budgetvalue

import android.content.res.AssetManager
import javax.inject.Inject

/**
 * I could not figure out how to launch an Activity in androidTest/, but.. this is a workaround so at least the assets can be defined in androidTest/
 */
open class AndroidTestAssetOwner @Inject constructor() {
    open val assets: AssetManager get() = error("Should only be used in androidTest/")
}