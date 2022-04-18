package com.tminus1010.buva.core_testing

import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.navigation.NavController
import com.tminus1010.buva.PlaygroundActivity
import com.tminus1010.buva.all_layers.extensions.asObservable2
import com.tminus1010.buva.app.ImportTransactions
import com.tminus1010.buva.app.InitApp
import com.tminus1010.buva.data.FuturesRepo
import com.tminus1010.buva.data.HasAppBeenInitializedRepo
import com.tminus1010.buva.domain.CategoryAmounts
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.domain.TransactionMatcher
import com.tminus1010.buva.ui.all_features.view_model_item.MenuVMItem
import com.tminus1010.buva.ui.host.GetExtraMenuItemPartials
import com.tminus1010.buva.ui.transactions.TransactionBlocksFrag
import com.tminus1010.tmcommonkotlin.core.generateUniqueID
import com.tminus1010.tmcommonkotlin.rx3.extensions.toSingle
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
    fun getExtraMenuItemPartials(hasAppBeenInitializedRepo: HasAppBeenInitializedRepo, initApp: InitApp, importTransactions: ImportTransactions, futuresRepo: FuturesRepo, application: Application) = object : GetExtraMenuItemPartials() {
        override fun invoke(nav: BehaviorSubject<NavController>): Array<MenuVMItem> {
            return arrayOf(
                MenuVMItem(
                    title = "Playground",
                    onClick = { application.startActivity(Intent(application, PlaygroundActivity::class.java).apply { flags += FLAG_ACTIVITY_NEW_TASK }) }
                ),
                MenuVMItem("Redo App Init") {
                    GlobalScope.launch {
                        hasAppBeenInitializedRepo.pushAppInitBool2(false)
                        initApp()
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
                    onClick = { TransactionBlocksFrag.navTo(nav.value!!) },
                )
            )
        }
    }
}