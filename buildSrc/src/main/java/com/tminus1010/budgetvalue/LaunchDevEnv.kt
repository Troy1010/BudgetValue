package com.tminus1010.budgetvalue

import org.gradle.api.tasks.Exec
import java.time.Duration
import javax.inject.Inject

open class LaunchDevEnv @Inject constructor(adbAbsolutePath: String, nameOfDevEnv: String) : Exec() {
    init {
        commandLine = listOf(adbAbsolutePath, "shell", "am", "instrument", "-w", "-m", "-e", "debug", "false", "-e", "class", "com.tminus1010.budgetvalue.__devEnvs.$nameOfDevEnv", "com.tminus1010.budgetvalue.test/com.tminus1010.budgetvalue.__core_testing.CustomTestRunner")
        // Normally, this task will not quit until the test is complete. However, the test never completes, but we still want the task to end.. so this timeout is a workaround.
        timeout.set(Duration.ofMillis(5000))
    }
}