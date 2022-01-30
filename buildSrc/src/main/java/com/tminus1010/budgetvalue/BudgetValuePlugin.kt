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
            tasks.register("launchDevEnv_Main", LaunchDevEnv::class.java, budgetValuePluginSettings.adbAbsolutePath, "DevEnv_Main")
                .configure { group = "aa" }
            tasks.register("launchDevEnv_UnlockedFeatures", LaunchDevEnv::class.java, budgetValuePluginSettings.adbAbsolutePath, "DevEnv_UnlockedFeatures")
                .configure { group = "aa" }
            tasks.register("quitApp", QuitApp::class.java, budgetValuePluginSettings.adbAbsolutePath)
            tasks.tryRegisterOrderedPair("installDebug", "launchApp")
            tasks.tryRegisterOrderedPair("clean", "installDebug_launchApp")
            tasks.tryRegisterOrderedPair("installDebug", "launchDevEnv_Main")
            tasks.tryRegisterOrderedPair("installDebugAndroidTest", "installDebug_launchDevEnv_Main")
            tasks.tryRegisterOrderedPair("clean", "installDebugAndroidTest_installDebug_launchDevEnv_Main")
            tasks.register("easyRebuildAndLaunchApp") {
                description = "Launches slowly, but reliably"
                group = "easy"
                dependsOn(tasks.named("clean_installDebug_launchApp"))
            }
            tasks.register("easyRebuildAndLaunchDevEnv_Main") {
                description = "Launches slowly, but reliably. When successful, it will throw a timeout failure.. just ignore it."
                group = "easy"
                dependsOn(tasks.named("clean_installDebugAndroidTest_installDebug_launchDevEnv_Main"))
            }
        }
    }
}