package com.tminus1010.budgetvalue.all_features.ui.all_features.model

import android.content.Context

sealed class UnformattedString {
    abstract fun getString(context: Context): String
    class WithCoreString(private val s: String, private val args: List<UnformattedString>? = null) : UnformattedString() {
        override fun getString(context: Context): String {
            return s
                .let {
                    if (args == null) it else
                        formatAndThenAppend(it, args.map { it.getString(context) })
                }
        }
    }

    class WithCoreStringID(private val stringID: Int, private val args: List<UnformattedString>? = null) : UnformattedString() {
        override fun getString(context: Context): String {
            return context.getString(stringID)
                .let {
                    if (args == null) it else
                        formatAndThenAppend(it, args.map { it.getString(context) })
                }
        }
    }

    companion object {
        // TODO("Trying to format an int fails. How to tell if it's an int to format in, or a string id..?")
        private fun fromAnyToUnformattedString(any: Any): UnformattedString {
            return when (any) {
                is String -> WithCoreString(any)
                is Int -> WithCoreStringID(any)
                is UnformattedString -> any
                else -> error("Unhandled type for arg:$any")
            }
        }

        operator fun invoke(s: String, vararg args: Any): WithCoreString {
            return WithCoreString(
                s = s,
                args = args.map(Companion::fromAnyToUnformattedString)
            )
        }

        operator fun invoke(stringID: Int, vararg args: Any): WithCoreStringID {
            return WithCoreStringID(
                stringID = stringID,
                args = args.map(Companion::fromAnyToUnformattedString)
            )
        }

        //

        private fun formatAndThenAppend(s: String, strings: List<String>): String {
            val numOfFormatArgs = howManyFormatArgsInString(s)
            var returning = String.format(s, *strings.take(numOfFormatArgs).toTypedArray())
            strings.drop(numOfFormatArgs).forEach {
                returning += it
            }
            return returning
        }

        private val regex = Regex("""%[0-9]?[${'$'}]?s""")

        // TODO("Also search for %d and %i")
        private fun howManyFormatArgsInString(s: String): Int {
            return regex.findAll(s).count()
        }
    }
}