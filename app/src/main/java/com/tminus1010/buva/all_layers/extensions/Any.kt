package com.tminus1010.buva.all_layers.extensions


fun <T> T?.ifNull(lambda: () -> T?): T? {
    return this ?: lambda()
}