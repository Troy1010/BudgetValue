package com.tminus1010.budgetvalue

class AdbAbsolutePathWasNullException : Exception(
    """
        |adbAbsolutePath was null. It must be set in build.gradle, for example:
        |   budgetValuePluginExtension {
        |       adbAbsolutePath = android.getAdbExecutable().absolutePath
        |   }
        """.trimMargin()
)