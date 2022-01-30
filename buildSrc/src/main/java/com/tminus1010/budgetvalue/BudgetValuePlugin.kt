package com.tminus1010.budgetvalue

import org.gradle.api.Plugin
import org.gradle.api.Project
import tmextensions.tryRegisterOrderedPair

open class BudgetValuePlugin : Plugin<Project> {
    open class Settings {
        var adbAbsolutePath: String = ""
            get() {
                return field.also { if (it == "") throw AdbAbsolutePathWasEmptyException() }
            }
    }

    override fun apply(project: Project) {
        val budgetValuePluginSettings = project.extensions.create("budgetValuePluginSettings", Settings::class.java)
        project.afterEvaluate {
            tasks.register("launchApp", LaunchApp::class.java, budgetValuePluginSettings.adbAbsolutePath)
            tasks.register("launchDevEnv_Main", LaunchDevEnv_Main::class.java, budgetValuePluginSettings.adbAbsolutePath)
            tasks.register("quitApp", QuitApp::class.java, budgetValuePluginSettings.adbAbsolutePath)
            tasks.tryRegisterOrderedPair("installDebug", "launchApp")
            tasks.tryRegisterOrderedPair("clean", "installDebug_launchApp")
            tasks.tryRegisterOrderedPair("installDebug", "launchDevEnv_Main")
            tasks.tryRegisterOrderedPair("clean", "installDebug_launchDevEnv_Main")
            tasks.register("easyRebuildAndLaunchApp") {
                description = "Launches app slowly, but reliably"
                group = "easy"
                dependsOn(tasks.named("clean_installDebug_launchApp"))
            }
            tasks.register("easyRebuildAndLaunchDevEnv_Main") {
                description = "Launches DevEnv_Main slowly, but reliably"
                group = "easy"
                dependsOn(tasks.named("clean_installDebug_launchDevEnv_Main"))
            }
        }
    }
}