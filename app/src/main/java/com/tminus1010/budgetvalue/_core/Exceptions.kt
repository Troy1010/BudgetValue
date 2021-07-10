package com.tminus1010.budgetvalue._core

class ImportFailedException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause)
class TestException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause)
class InvalidCategoryNameException : Exception("InvalidCategoryName")
class InvalidCategoryAmounts(msg: String) : RuntimeException(msg)