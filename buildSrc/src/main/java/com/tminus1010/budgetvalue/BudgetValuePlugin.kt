package com.tminus1010.budgetvalue

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskContainer
import tmextensions.tryRegisterOrderedPair

open class BudgetValuePlugin : Plugin<Project> {
    open class Settings {
        var adbAbsolutePath: String = ""
            get() {
                return field.also { if (it == "") throw AdbAbsolutePathWasEmptyException() }
            }
    }

    lateinit var budgetValuePluginSettings: Settings
    override fun apply(project: Project) {
        budgetValuePluginSettings = project.extensions.create("budgetValuePluginSettings", Settings::class.java)
        project.afterEvaluate {
            // # easyRebuildAndLaunchApp, easyInstallAndLaunchApp
            tasks.register("launchApp", LaunchApp::class.java, budgetValuePluginSettings.adbAbsolutePath)
            tasks.tryRegisterOrderedPair("installDebug", "launchApp")
            tasks.tryRegisterOrderedPair("clean", "installDebug_launchApp")
            tasks.register("easyRebuildAndLaunchApp") {
                description = "Launches slowly, but reliably"
                group = "easy"
                dependsOn(tasks.named("clean_installDebug_launchApp"))
            }
            tasks.register("easyInstallAndLaunchApp") {
                description = "Launches more quickly, but less reliably"
                group = "easy"
                dependsOn(tasks.named("installDebug_launchApp"))
            }
            //
            registerEasyRebuildAndLaunchDevEnv(tasks, "DevEnv_Main")
            registerEasyInstallAndLaunchDevEnv(tasks, "DevEnv_Main")
            registerEasyRebuildAndLaunchDevEnv(tasks, "DevEnv_UnlockedFeatures")
            registerEasyInstallAndLaunchDevEnv(tasks, "DevEnv_UnlockedFeatures")
        }
    }

    private fun registerEasyRebuildAndLaunchDevEnv(tasks: TaskContainer, nameOfDevEnv: String) {
        runCatching { tasks.register("launch$nameOfDevEnv", LaunchDevEnv::class.java, budgetValuePluginSettings.adbAbsolutePath, nameOfDevEnv) }
        tasks.tryRegisterOrderedPair("installDebug", "launch$nameOfDevEnv")
        tasks.tryRegisterOrderedPair("installDebugAndroidTest", "installDebug_launch$nameOfDevEnv")
        tasks.tryRegisterOrderedPair("clean", "installDebugAndroidTest_installDebug_launch$nameOfDevEnv")
        tasks.register("easyRebuildAndLaunch$nameOfDevEnv") {
            description = "Launches slowly, but reliably.\nWhen successful, it will throw a timeout failure.. just ignore it."
            group = "easy"
            dependsOn(tasks.named("clean_installDebugAndroidTest_installDebug_launch$nameOfDevEnv"))
        }
    }

    private fun registerEasyInstallAndLaunchDevEnv(tasks: TaskContainer, nameOfDevEnv: String) {
        runCatching { tasks.register("launch$nameOfDevEnv", LaunchDevEnv::class.java, budgetValuePluginSettings.adbAbsolutePath, nameOfDevEnv) }
        tasks.tryRegisterOrderedPair("installDebug", "launch$nameOfDevEnv")
        tasks.tryRegisterOrderedPair("installDebugAndroidTest", "installDebug_launch$nameOfDevEnv")
        tasks.register("easyInstallAndLaunch$nameOfDevEnv") {
            description = "Launches quickly (if build cache is available), but less reliably.\nWhen successful, it will throw a timeout failure.. just ignore it."
            group = "easy"
            dependsOn(tasks.named("installDebugAndroidTest_installDebug_launch$nameOfDevEnv"))
        }
    }
}