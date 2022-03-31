package com.tminus1010.budgetvalue.__core_testing

import android.app.Application
import androidx.navigation.NavController
import com.tminus1010.budgetvalue._unrestructured.transactions.view.TransactionBlockCompletionFrag
import com.tminus1010.budgetvalue.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.app.ImportTransactions
import com.tminus1010.budgetvalue.app.TryInitApp
import com.tminus1010.budgetvalue.data.AppInitRepo
import com.tminus1010.budgetvalue.data.FuturesRepo
import com.tminus1010.budgetvalue.domain.CategoryAmounts
import com.tminus1010.budgetvalue.domain.Transaction
import com.tminus1010.budgetvalue.domain.TransactionMatcher
import com.tminus1010.budgetvalue.ui.all_features.view_model_item.MenuVMItem
import com.tminus1010.budgetvalue.ui.host.GetExtraMenuItemPartials
import com.tminus1010.tmcommonkotlin.misc.generateUniqueID
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ExtraMenuItemPartialsModule {
    @Provides
    @Singleton
    fun getExtraMenuItemPartials(appInitRepo: AppInitRepo, tryInitApp: TryInitApp, importTransactions: ImportTransactions, futuresRepo: FuturesRepo, application: Application) = object : GetExtraMenuItemPartials() {
        override fun invoke(nav: BehaviorSubject<NavController>): Array<MenuVMItem> {
            return arrayOf(
                MenuVMItem("Redo App Init") {
                    GlobalScope.launch {
                        appInitRepo.pushAppInitBool2(false)
                        tryInitApp()
                    }
                },
                MenuVMItem("Import Transaction for Future") {
                    futuresRepo.futures.asObservable2().toSingle()
                        .flatMapCompletable { futures ->
                            val firstSearchTotal = futures
                                .find { it.onImportTransactionMatcher is TransactionMatcher.ByValue }
                                ?.let { it.onImportTransactionMatcher as TransactionMatcher.ByValue }
                                ?.searchTotal
                            if (firstSearchTotal != null)
                                Completable.fromAction {
                                    runBlocking {
                                        importTransactions(
                                            listOf(
                                                Transaction(
                                                    date = LocalDate.of(2018, 1, 1),
                                                    description = "Mock description of transaction",
                                                    amount = firstSearchTotal,
                                                    categoryAmounts = CategoryAmounts(),
                                                    categorizationDate = null,
                                                    id = generateUniqueID(),
                                                )
                                            )
                                        )
                                    }
                                }
                            else
                                Completable.fromCallable { application.easyToast("No TotalFutures found") }.subscribeOn(AndroidSchedulers.mainThread())
                        }
                        .subscribe()
                },
                MenuVMItem(
                    title = "TransactionBlocks",
                    onClick = { TransactionBlockCompletionFrag.navTo(nav.value!!) },
                )
            )
        }
    }
}