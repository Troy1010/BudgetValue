package com.tminus1010.budgetvalue

class ImportFailedException(msg: String? = null, cause: Throwable? = null) : Exception(msg, cause) {
    constructor(cause: Throwable?) : this(null, cause)
}

class TestException : Exception()