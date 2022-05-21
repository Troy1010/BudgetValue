package com.tminus1010.buva.ui.transaction_matcher_details

import androidx.lifecycle.*
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.extensions.onNext
import com.tminus1010.buva.all_layers.extensions.toMoneyBigDecimal
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.Future
import com.tminus1010.buva.domain.TransactionMatcher
import com.tminus1010.buva.ui.all_features.TransactionMatcherPresentationFactory
import com.tminus1010.buva.ui.all_features.model.SearchType
import com.tminus1010.buva.ui.all_features.view_model_item.*
import com.tminus1010.buva.ui.set_search_texts.SetSearchTextsSharedVM
import com.tminus1010.tmcommonkotlin.core.tryOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class TransactionMatcherDetailsVM @Inject constructor(
    savedStateHandle: SavedStateHandle,
    transactionMatcherPresentationFactory: TransactionMatcherPresentationFactory,
    private val setSearchTextsSharedVM: SetSearchTextsSharedVM,
) : ViewModel() {
    // # User Intents
    fun userSetSearchType(searchType: SearchType) {
        setTransactionMatcher(
            when (searchType) {
                SearchType.DESCRIPTION -> TransactionMatcher.Multi(setSearchTextsSharedVM.searchTexts.map { TransactionMatcher.SearchText(it) })
                SearchType.DESCRIPTION_AND_TOTAL -> TransactionMatcher.Multi(setSearchTextsSharedVM.searchTexts.map { TransactionMatcher.SearchText(it) }.plus(TransactionMatcher.ByValue(total.value)))
                SearchType.TOTAL -> TransactionMatcher.ByValue(total.value)
                SearchType.NONE -> null
            }
        )
    }

    fun userSetTotalGuess(s: String) {
        total.onNext(s.toMoneyBigDecimal())
    }

    fun userTryNavToSetSearchTexts() {
        navToSetSearchTexts.onNext()
    }

    // # Internal
    // TODO: Need to take KEY1
    val category = tryOrNull { savedStateHandle.getLiveData<Category>(KEY1) }
    val future = tryOrNull { savedStateHandle.getLiveData<Future>(KEY1) }
    val transactionMatcher: LiveData<TransactionMatcher?>
        get() {
            return when {
                category != null -> category.map { it.onImportTransactionMatcher }
                future != null -> future.map { it.onImportTransactionMatcher }
                else -> error("no transaction matcher available..?")
            }
        }

    fun setTransactionMatcher(transactionMatcher: TransactionMatcher?) {
        when {
            category != null -> category.value = category.value?.copy(onImportTransactionMatcher = transactionMatcher)
            future != null -> future.value = future.value?.copy(onImportTransactionMatcher = transactionMatcher)
            else -> error("no transaction matcher available..?")
        }
    }
    private val total = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)

    // # Events
    val navToSetSearchTexts = MutableSharedFlow<Unit>()

    // # State
    val transactionMatcherDetailsTableView =
        transactionMatcher.asFlow().map { transactionMatcher ->
            TableViewVMItem(
                recipeGrid = listOfNotNull(
                    listOf(
                        TextPresentationModel(TextPresentationModel.Style.TWO, text1 = "Search Type"),
                        SpinnerVMItem(
                            values = SearchType.values(),
                            initialValue = transactionMatcherPresentationFactory.searchType(transactionMatcher),
                            onNewItem = ::userSetSearchType,
                        ),
                    ),
                    if (sequenceOf(SearchType.TOTAL, SearchType.DESCRIPTION_AND_TOTAL).any { it == transactionMatcherPresentationFactory.searchType(transactionMatcher) })
                        listOf(
                            TextPresentationModel(
                                style = TextPresentationModel.Style.TWO,
                                text1 = transactionMatcherPresentationFactory.totalTitle(transactionMatcher)
                            ),
                            MoneyEditVMItem(text1 = total.value.toString(), onDone = ::userSetTotalGuess),
                        ) else null,
                    if (transactionMatcherPresentationFactory.hasSearchTexts(transactionMatcher))
                        listOf(
                            TextPresentationModel(style = TextPresentationModel.Style.TWO, text1 = "Search Texts"),
                            ButtonVMItem(title = "View Search Texts", onClick = ::userTryNavToSetSearchTexts),
                        )
                    else null,
                ),
                shouldFitItemWidthsInsideTable = true,
            )
        }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)
}