package com.tminus1010.buva

import org.gradle.api.tasks.Exec
import javax.inject.Inject

open class LaunchApp @Inject constructor(adbAbsolutePath: String) : Exec() {
    init {
        commandLine = listOf(adbAbsolutePath, "shell", "monkey", "-p", "com.tminus1010.buva", "-c", "android.intent.category.LAUNCHER", "1")
    }
}