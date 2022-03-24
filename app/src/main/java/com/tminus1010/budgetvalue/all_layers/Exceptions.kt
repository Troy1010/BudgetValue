package com.tminus1010.budgetvalue.all_layers

class ImportFailedException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause)
class TestException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause)
class InvalidCategoryNameException : Exception("InvalidCategoryName")
class InvalidCategoryAmounts(msg: String) : RuntimeException(msg)
class InvalidSearchText(msg: String) : RuntimeException(msg)
class NoDescriptionEnteredException : Exception()