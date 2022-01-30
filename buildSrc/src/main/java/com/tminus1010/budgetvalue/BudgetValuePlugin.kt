package com.tminus1010.budgetvalue

import org.gradle.api.Plugin
import org.gradle.api.Project
import tmextensions.tryRegisterOrderedPair

open class BudgetValuePlugin : Plugin<Project> {
    open class Extension {
        var adbAbsolutePath: String? = null
    }

    override fun apply(project: Project) {
        val budgetValuePluginExtension = project.extensions.create("budgetValuePluginExtension", Extension::class.java)
        project.afterEvaluate {
            tasks.register("launchApp", LaunchApp::class.java, budgetValuePluginExtension.adbAbsolutePath ?: throw AdbAbsolutePathWasNullException())
                .configure { group = "adb" }
            tasks.register("launchDevEnv_Main", LaunchDevEnv_Main::class.java, budgetValuePluginExtension.adbAbsolutePath ?: throw AdbAbsolutePathWasNullException())
                .configure { group = "adb" }
            tasks.register("quitApp", QuitApp::class.java, budgetValuePluginExtension.adbAbsolutePath ?: throw AdbAbsolutePathWasNullException())
                .configure { group = "adb" }
            tasks.tryRegisterOrderedPair("installDebug", "launchApp")
            tasks.tryRegisterOrderedPair("clean_uninstallDebug", "installDebug_launchApp")
                .configure { group = "combo" }
            tasks.tryRegisterOrderedPair("uninstallDebug", "installDebug_launchApp")
                .configure { group = "combo" }
            tasks.tryRegisterOrderedPair("installDebug", "launchDevEnv_Main")
            tasks.tryRegisterOrderedPair("clean_uninstallDebug", "installDebug_launchDevEnv_Main")
                .configure { group = "combo" }
        }
    }
}