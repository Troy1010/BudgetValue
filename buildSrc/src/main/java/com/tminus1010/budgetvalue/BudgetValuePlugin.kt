package com.tminus1010.budgetvalue

import org.gradle.api.Plugin
import org.gradle.api.Project

open class BudgetValuePlugin : Plugin<Project> {
    open class BudgetValuePluginExtension {
        var adbAbsolutePath: String? = null
    }

    override fun apply(project: Project) {
        val budgetValuePluginExtension = project.extensions.create("budgetValuePluginExtension", BudgetValuePluginExtension::class.java)
        project.afterEvaluate {
            print(budgetValuePluginExtension.adbAbsolutePath)
        }
    }
}