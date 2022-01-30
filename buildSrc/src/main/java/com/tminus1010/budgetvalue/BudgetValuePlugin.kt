package com.tminus1010.budgetvalue

import org.gradle.api.Plugin
import org.gradle.api.Project
import tmextensions.tryRegisterOrderedPair

open class BudgetValuePlugin : Plugin<Project> {
    open class BudgetValuePluginExtension {
        var adbAbsolutePath: String? = null
    }

    override fun apply(project: Project) {
        val budgetValuePluginExtension = project.extensions.create("budgetValuePluginExtension", BudgetValuePluginExtension::class.java)
        project.afterEvaluate {
            tasks.register("launchApp", LaunchApp::class.java, budgetValuePluginExtension.adbAbsolutePath ?: throw AdbAbsolutePathWasNullException())
                .configure { group = "adb" }
            tasks.register("launchDevEnv_Main", LaunchDevEnv_Main::class.java, budgetValuePluginExtension.adbAbsolutePath ?: throw AdbAbsolutePathWasNullException())
                .configure { group = "adb" }
            tasks.register("quitApp", Quit::class.java, budgetValuePluginExtension.adbAbsolutePath ?: throw AdbAbsolutePathWasNullException())
                .configure { group = "adb" }
            tasks.tryRegisterOrderedPair("quitApp", "launchApp")
                .configure { group = "adb" }
            tasks.tryRegisterOrderedPair("quitApp", "launchDevEnv_Main")
                .configure { group = "adb" }
        }
    }
}