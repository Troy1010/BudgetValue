package com.tminus1010.buva

import org.gradle.api.Plugin
import org.gradle.api.Project

open class BuvaPlugin : Plugin<Project> {
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
            tasks.register("launch_app", LaunchApp::class.java, budgetValuePluginSettings.adbAbsolutePath)
                .configure { setMustRunAfter(listOf("installDebug")) }
            tasks.register("launch_app_speed5") {
                group = "budgetvalue"
                dependsOn("installDebug", "launch_app")
            }
            tasks.register("launch_app_speed3") {
                group = "budgetvalue"
                dependsOn("clean", "installDebug", "launch_app")
            }
            // # Register launchDevEnvs
            layout.projectDirectory.dir("src/androidTest/java/com/tminus1010/buva/devEnvs").asFileTree.filter { it.name.startsWith("DevEnv") }.map { it.name.dropLast(3) }.forEach {
                tasks.register("launch_$it", LaunchDevEnv::class.java, budgetValuePluginSettings.adbAbsolutePath, it)
                    .configure { setMustRunAfter(listOf("installDebug", "installDebugAndroidTest")) }
                tasks.register("launch_${it}_speed5") {
                    description = "Launches quickly (if build cache is available), but less reliably. When successful, it will throw a timeout failure.. just ignore it."
                    group = "budgetvalue"
                    dependsOn("installDebug", "installDebugAndroidTest", "launch_$it")
                }
                tasks.register("launch_${it}_speed3") {
                    description = "Launches slowly, but reliably. When successful, it will throw a timeout failure.. just ignore it."
                    group = "budgetvalue"
                    dependsOn("clean", "installDebug", "installDebugAndroidTest", "launch_$it")
                }
            }
        }
    }
}