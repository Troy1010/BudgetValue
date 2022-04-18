package com.tminus1010.buva

import org.gradle.api.tasks.Exec
import java.time.Duration
import javax.inject.Inject

open class LaunchDevEnv @Inject constructor(adbAbsolutePath: String, nameOfDevEnv: String) : Exec() {
    init {
        commandLine = listOf(adbAbsolutePath, "shell", "am", "instrument", "-w", "-m", "-e", "debug", "false", "-e", "class", "com.tminus1010.buva.__devEnvs.$nameOfDevEnv", "com.tminus1010.buva.test/com.tminus1010.buva.__core_testing.CustomTestRunner")
        // Normally, this task will not quit until the test is complete. However, the test never completes, and we still want the task to end.. so this timeout is a workaround.
        timeout.set(Duration.ofMillis(5000))
    }
}