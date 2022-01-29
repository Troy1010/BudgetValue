package com.tminus1010.budgetvalue

import org.gradle.api.tasks.Exec
import javax.inject.Inject

open class LaunchDevEnv_Main @Inject constructor(adbAbsolutePath: String) : Exec() {
    init {
        commandLine = listOf(adbAbsolutePath, "shell", "am", "instrument", "-w", "-m", "-e", "debug", "false", "-e", "class", "com.tminus1010.budgetvalue.__devEnvs.DevEnv_Main#main", "com.tminus1010.budgetvalue.test/com.tminus1010.budgetvalue.__core_testing.CustomTestRunner")
    }
}