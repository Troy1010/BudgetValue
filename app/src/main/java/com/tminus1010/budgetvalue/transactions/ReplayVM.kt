package com.tminus1010.budgetvalue.transactions

import androidx.lifecycle.disposables
import com.tminus1010.budgetvalue._core.extensions.cold
import com.tminus1010.budgetvalue._core.extensions.nonLazyCache
import com.tminus1010.budgetvalue._core.middleware.ColdObservable
import com.tminus1010.budgetvalue._core.middleware.ui.ButtonVMItem
import com.tminus1010.budgetvalue._core.models.CategoryAmountFormulas
import com.tminus1010.budgetvalue.categories.CategorySelectionVM
import com.tminus1010.budgetvalue.categories.ICategoryParser
import com.tminus1010.budgetvalue.categories.models.Category
import com.tminus1010.budgetvalue.replay_or_future.CategoryAmountFormulaVMItemsBaseVM
import com.tminus1010.budgetvalue.replay_or_future.data.ReplaysRepo
import com.tminus1010.budgetvalue.replay_or_future.models.BasicReplay
import com.tminus1010.tmcommonkotlin.rx.extensions.observe
import com.tminus1010.tmcommonkotlin.rx.extensions.value
import com.tminus1010.tmcommonkotlin.rx.replayNonError
import com.tminus1010.tmcommonkotlin.tuple.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ReplayVM @Inject constructor(
    private val replaysRepo: ReplaysRepo,
    private val errorSubject: Subject<Throwable>,
    override val categoryParser: ICategoryParser,
) : CategoryAmountFormulaVMItemsBaseVM() {
    // # Input
    private val _replay = BehaviorSubject.create<BasicReplay>()
    fun setup(_replay: BasicReplay, categorySelectionVM: CategorySelectionVM) {
        this.categorySelectionVM = categorySelectionVM
        this._replay.onNext(_replay)
    }

    fun userSaveReplay(name: String) {
        val _replay = BasicReplay(
            name = name,
            searchTexts = searchTexts.value!!,
            categoryAmountFormulas = categoryAmountFormulas.value!!.filter { !it.value.isZero() },
            fillCategory = _fillCategory.value.first!!,
        )
        replaysRepo.update(_replay)
            .observe(disposables,
                onComplete = { navUp.onNext(Unit) },
                onError = errorSubject::onNext
            )
    }

    fun userDeleteReplay(replayName: String) {
        replaysRepo.delete(replayName)
            .observe(disposables,
                onComplete = { navUp.onNext(Unit) },
                onError = errorSubject::onNext
            )
    }

    private val userAddSearchText = PublishSubject.create<String>()
    fun userAddSearchText(searchText: String) {
        userAddSearchText.onNext(searchText)
    }

    // # Internal

    // # Output
    override val _totalGuess: ColdObservable<BigDecimal> =
        Observable.just(BigDecimal.ZERO)
            .cold()
    val replay: Observable<BasicReplay> = _replay!!
    val userAddSearchTexts =
        userAddSearchText
            .scan(listOf<String>()) { acc, v -> acc.plus(v) }
            .nonLazyCache(disposables)
    val searchTexts =
        Observable.combineLatest(replay, userAddSearchTexts)
        { replay, userAddSearchTexts ->
            replay.searchTexts.plus(userAddSearchTexts)
        }!!
            .replayNonError(1)

    override val _selectedCategories =
        Observable.combineLatest(super._selectedCategories, replay)
        { selectedCategories, replay ->
            selectedCategories.plus(replay.fillCategory)
        }
    val amountOfSearchTexts =
        searchTexts.map { it.size.toString() }
    override val _fillCategory =
        Observable.combineLatest(super._fillCategory, replay)
        { (fillCategory), replay ->
            Box<Category?>(fillCategory ?: replay.fillCategory)
        }
            .cold()
    override val _categoryAmountFormulas =
        Observable.combineLatest(super._categoryAmountFormulas, replay)
        { categoryAmountFormulas, replay ->
            replay.categoryAmountFormulas
                .plus(categoryAmountFormulas)
        }
            .map { CategoryAmountFormulas(it) }
            .cold()
    val defaultAmount =
        Observable.combineLatest(categoryAmountFormulas, totalGuess)
        { categoryAmountFormulas, total ->
            categoryAmountFormulas.defaultAmount(total).toString()
        }!!
    val navUp = PublishSubject.create<Unit>()!!
    val navToSelectTransactionName = PublishSubject.create<Unit>()!!
    val deleteReplayDialogBox = PublishSubject.create<Unit>()!!
    val buttons = listOf(
        ButtonVMItem(
            title = "Delete Replay",
            onClick = { deleteReplayDialogBox.onNext(Unit) }
        ),
        ButtonVMItem(
            title = "Add Search Text",
            onClick = { navToSelectTransactionName.onNext(Unit) }
        ),
        ButtonVMItem(
            title = "Save Replay",
            onClick = { userSaveReplay(replay.value!!.name) }
        ),
    ).reversed()
}