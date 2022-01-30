package com.tminus1010.budgetvalue

import org.gradle.api.Plugin
import org.gradle.api.Project
import tmextensions.tryRegisterOrderedPair

open class BudgetValuePlugin : Plugin<Project> {
    open class Settings {
        var adbAbsolutePath: String? = null
    }

    override fun apply(project: Project) {
        val budgetValuePluginSettings = project.extensions.create("budgetValuePluginSettings", Settings::class.java)
        project.afterEvaluate {
            tasks.register("launchApp", LaunchApp::class.java, budgetValuePluginSettings.adbAbsolutePath ?: throw AdbAbsolutePathWasNullException())
            tasks.register("launchDevEnv_Main", LaunchDevEnv_Main::class.java, budgetValuePluginSettings.adbAbsolutePath ?: throw AdbAbsolutePathWasNullException())
            tasks.register("quitApp", QuitApp::class.java, budgetValuePluginSettings.adbAbsolutePath ?: throw AdbAbsolutePathWasNullException())
            tasks.tryRegisterOrderedPair("installDebug", "launchApp")
                .configure { group = "install" }
            tasks.tryRegisterOrderedPair("clean_uninstallDebug", "installDebug_launchApp")
                .configure { group = "combo" }
            tasks.tryRegisterOrderedPair("uninstallDebug", "installDebug_launchApp")
                .configure { group = "combo" }
            tasks.tryRegisterOrderedPair("installDebug", "launchDevEnv_Main")
                .configure { group = "install" }
            tasks.tryRegisterOrderedPair("clean_uninstallDebug", "installDebug_launchDevEnv_Main")
                .configure { group = "combo" }
        }
    }
}