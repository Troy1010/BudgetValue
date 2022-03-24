package com.tminus1010.budgetvalue.ui.set_search_texts

import com.tminus1010.budgetvalue.framework.source_objects.SourceList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetSearchTextsSharedVM @Inject constructor() {
    val searchTexts = SourceList<String>()
}