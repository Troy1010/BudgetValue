package com.tminus1010.buva.all_layers

class ImportFailedException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause)
class TestException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause)
class InvalidCategoryNameException : Exception("InvalidCategoryName")
class InvalidCategoryAmounts(msg: String) : RuntimeException(msg)
class InvalidSearchText(msg: String) : RuntimeException(msg)
class InvalidStateException(msg: String? = null) : RuntimeException(msg)