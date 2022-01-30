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
            tasks.register("installAndLaunchApp") {
                description = "Launches more quickly (if build cache is available), but less reliably"
                group = "launch"
                dependsOn(tasks.named("installDebug_launchApp"))
            }
            // easyRebuildAndLaunchDevEnvs, easyInstallAndLaunchDevEnvs
            listOf("DevEnv_Main", "DevEnv_UnlockedFeatures").forEach {
                tasks.register("launch$it", LaunchDevEnv::class.java, budgetValuePluginSettings.adbAbsolutePath, it)
                tasks.tryRegisterOrderedPair("installDebug", "launch$it")
                tasks.tryRegisterOrderedPair("installDebugAndroidTest", "installDebug_launch$it") // TODO: Installs can happen simultaneously..
                tasks.tryRegisterOrderedPair("clean", "installDebugAndroidTest_installDebug_launch$it")
                tasks.register("easyRebuildAndLaunch$it") {
                    description = "Launches slowly, but reliably. When successful, it will throw a timeout failure.. just ignore it."
                    group = "easy"
                    dependsOn(tasks.named("clean_installDebugAndroidTest_installDebug_launch$it"))
                }
                tasks.register("installAndLaunch$it") {
                    description = "Launches quickly (if build cache is available), but less reliably. When successful, it will throw a timeout failure.. just ignore it."
                    group = "launch"
                    dependsOn(tasks.named("installDebugAndroidTest_installDebug_launch$it"))
                }
            }
        }
    }
}