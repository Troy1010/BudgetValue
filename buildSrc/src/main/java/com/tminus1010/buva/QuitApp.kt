package com.tminus1010.buva

import org.gradle.api.tasks.Exec
import javax.inject.Inject

open class QuitApp @Inject constructor(adbAbsolutePath: String) : Exec() {
    init {
        commandLine = listOf(adbAbsolutePath, "shell", "am", "force-stop", "com.tminus1010.buva")
    }
}