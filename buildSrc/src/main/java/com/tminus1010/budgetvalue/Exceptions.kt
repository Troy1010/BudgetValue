package com.tminus1010.budgetvalue

class AdbAbsolutePathWasEmptyException : Exception(
    """
        |adbAbsolutePath was empty. It must be set in build.gradle, for example:
        |   budgetValuePluginExtension {
        |       adbAbsolutePath = android.getAdbExecutable().absolutePath
        |   }
        """.trimMargin()
)