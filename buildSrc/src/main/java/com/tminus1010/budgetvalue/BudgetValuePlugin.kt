package com.tminus1010.budgetvalue

import org.gradle.api.Plugin
import org.gradle.api.Project

open class BudgetValuePlugin : Plugin<Project> {
    var x = "x"
    override fun apply(project: Project) {
        project.afterEvaluate {
            print(x)
//            tasks.tryRegisterOrderedPair("clean", "assemble")
//                .configure { group = "build" }
//            tasks.tryRegisterOrderedPair("clean_assemble", "uninstallDebug")
//                .configure { group = "install" }
//            tasks.tryRegisterOrderedPair("clean_assemble_uninstallDebug", "installDebug")
//                .configure { group = "install" }
//            if (tasks.contains("publishToMavenLocal"))
//                tasks.tryRegisterOrderedPair("assemble", "publishToMavenLocal")
//                    .configure { group = "publishing" }
//
//
//            tasks.register("launchApp", LaunchApp, android.getAdbExecutable().absolutePath)
//                .configure { it.group = "adb" }
//            tasks.register("launchDevEnv_Main", LaunchDevEnv_Main, android.getAdbExecutable().absolutePath)
//                .configure { it.group = "adb" }
//            tasks.register("quitApp", Quit, android.getAdbExecutable().absolutePath)
//                .configure { it.group = "adb" }
//            TaskContainerKt.tryRegisterOrderedPair(tasks, "quitApp", "launchApp")
//                .configure { it.group = "adb" }
//            TaskContainerKt.tryRegisterOrderedPair(tasks, "quitApp", "launchDevEnv_Main")
//                .configure { it.group = "adb" }
        }
    }
}