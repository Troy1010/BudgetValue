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
            listOf("DevEnv_Main", "DevEnv_UnlockedFeatures").forEach {
                tasks.register("launch$it", LaunchDevEnv::class.java, budgetValuePluginSettings.adbAbsolutePath, it)
                tasks.tryRegisterOrderedPair("installDebug", "launch$it")
                tasks.tryRegisterOrderedPair("installDebugAndroidTest", "installDebug_launch$it")
                tasks.tryRegisterOrderedPair("clean", "installDebugAndroidTest_installDebug_launch$it")
                tasks.register("easyRebuildAndLaunch$it") {
                    description = "Launches slowly, but reliably.\nWhen successful, it will throw a timeout failure.. just ignore it."
                    group = "easy"
                    dependsOn(tasks.named("clean_installDebugAndroidTest_installDebug_launch$it"))
                }
                tasks.register("easyInstallAndLaunch$it") {
                    description = "Launches quickly (if build cache is available), but less reliably.\nWhen successful, it will throw a timeout failure.. just ignore it."
                    group = "easy"
                    dependsOn(tasks.named("installDebugAndroidTest_installDebug_launch$it"))
                }
            }
        }
    }
}