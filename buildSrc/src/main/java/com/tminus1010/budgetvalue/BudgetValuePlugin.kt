package com.tminus1010.budgetvalue

import org.gradle.api.Plugin
import org.gradle.api.Project

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
            // # Register launchApp
            tasks.register("launchApp", LaunchApp::class.java, budgetValuePluginSettings.adbAbsolutePath)
                .configure { setMustRunAfter(listOf(tasks.named("installDebug"))) }
            tasks.register("installAndLaunchApp") {
                description = "Launches more quickly (if build cache is available), but less reliably"
                group = "launch"
                dependsOn(tasks.named("installDebug"), tasks.named("launchApp"))
            }
            tasks.register("easyRebuildAndLaunchApp") {
                description = "Launches slowly, but reliably"
                group = "easy"
                dependsOn(tasks.named("clean"), tasks.named("installDebug"), tasks.named("launchApp"))
            }
            // # Register launchDevEnvs
            listOf("DevEnv_Main", "DevEnv_UnlockedFeatures").forEach {
                tasks.register("launch$it", LaunchDevEnv::class.java, budgetValuePluginSettings.adbAbsolutePath, it)
                    .configure { setMustRunAfter(listOf(tasks.named("installDebug"), tasks.named("installDebugAndroidTest"))) }
                tasks.register("installAndLaunch$it") {
                    description = "Launches quickly (if build cache is available), but less reliably. When successful, it will throw a timeout failure.. just ignore it."
                    group = "launch"
                    dependsOn(tasks.named("installDebug"), tasks.named("installDebugAndroidTest"), tasks.named("launch$it"))
                }
                tasks.register("easyRebuildAndLaunch$it") {
                    description = "Launches slowly, but reliably. When successful, it will throw a timeout failure.. just ignore it."
                    group = "easy"
                    dependsOn(tasks.named("clean"), tasks.named("installDebug"), tasks.named("installDebugAndroidTest"), tasks.named("launch$it"))
                }
            }
        }
    }
}