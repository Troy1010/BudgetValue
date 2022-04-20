package com.tminus1010.buva.ui.all_features.model

enum class SearchType(val displayStr: String) {
    DESCRIPTION_AND_TOTAL("Description and Total"), TOTAL("Total"), DESCRIPTION("Description"), NONE("None");

    override fun toString() = displayStr
}