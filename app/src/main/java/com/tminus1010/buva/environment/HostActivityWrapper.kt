package com.tminus1010.buva.environment

import android.annotation.SuppressLint
import com.tminus1010.buva.ui.host.HostActivity
import com.tminus1010.buva.ui.host.LaunchChooseFile
import javax.inject.Inject

class HostActivityWrapper @Inject constructor(
    private val launchChooseFile: LaunchChooseFile,
) {
    private val hostActivity get() = Companion.hostActivity ?: error("This class expects Companion.activity to be assigned")

    fun launchChooseFile() {
        launchChooseFile(hostActivity)
    }

    companion object {
        // This pattern can cause memory leaks. However, this project only has 1 HostActivity, so a memory leak is unlikely
        @SuppressLint("StaticFieldLeak")
        var hostActivity: HostActivity? = null
    }
}