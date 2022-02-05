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
                .configure { setMustRunAfter(listOf("installDebug")) }
            tasks.register("installAndLaunchApp") {
                description = "Launches more quickly (if build cache is available), but less reliably"
                group = "budgetvalue"
                dependsOn("installDebug", "launchApp")
            }
            tasks.register("rebuildAndLaunchApp") {
                description = "Launches slowly, but reliably"
                group = "budgetvalue"
                dependsOn("clean", "installDebug", "launchApp")
            }
            // # Register launchDevEnvs
            layout.projectDirectory.dir("src/androidTest/java/com/tminus1010/budgetvalue/__devEnvs").asFileTree.filter { it.name.startsWith("DevEnv") }.map { it.name.dropLast(3) }.forEach {
                tasks.register("launch$it", LaunchDevEnv::class.java, budgetValuePluginSettings.adbAbsolutePath, it)
                    .configure { setMustRunAfter(listOf("installDebug", "installDebugAndroidTest")) }
                tasks.register("installAndLaunch$it") {
                    description = "Launches quickly (if build cache is available), but less reliably. When successful, it will throw a timeout failure.. just ignore it."
                    group = "budgetvalue"
                    dependsOn("installDebug", "installDebugAndroidTest", "launch$it")
                }
                tasks.register("rebuildAndLaunch$it") {
                    description = "Launches slowly, but reliably. When successful, it will throw a timeout failure.. just ignore it."
                    group = "budgetvalue"
                    dependsOn("clean", "installDebug", "installDebugAndroidTest", "launch$it")
                }
            }
        }
    }
}