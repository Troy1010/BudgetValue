package com.tminus1010.budgetvalue.__core_testing

import android.app.Application
import androidx.navigation.NavController
import com.tminus1010.budgetvalue.all_features.all_layers.extensions.asObservable2
import com.tminus1010.budgetvalue.all_features.ui.all_features.model.MenuVMItem
import com.tminus1010.budgetvalue.all_features.ui.host.GetExtraMenuItemPartials
import com.tminus1010.budgetvalue.all_features.app.AppInitInteractor
import com.tminus1010.budgetvalue.all_features.data.repo.AppInitRepo
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStrategy
import com.tminus1010.budgetvalue.replay_or_future.domain.TotalFuture
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.budgetvalue.transactions.view.TransactionBlockCompletionFrag
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
import java.time.LocalDate
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ExtraMenuItemPartialsModule {
    @Provides
    @Singleton
    fun getExtraMenuItemPartials(appInitRepo: AppInitRepo, appInitInteractor: AppInitInteractor, transactionsInteractor: TransactionsInteractor, futuresRepo: FuturesRepo, application: Application) = object : GetExtraMenuItemPartials() {
        override fun invoke(nav: BehaviorSubject<NavController>): Array<MenuVMItem> {
            return arrayOf(
                MenuVMItem("Redo App Init") {
                    GlobalScope.launch {
                        appInitRepo.pushAppInitBool2(false)
                        appInitInteractor.tryInitializeApp()
                    }
                },
                MenuVMItem("Import Transaction for Future") {
                    futuresRepo.futures.asObservable2().toSingle()
                        .flatMapCompletable { futures ->
                            val firstSearchTotal = futures
                                .find { it is TotalFuture && it.terminationStrategy == TerminationStrategy.WAITING_FOR_MATCH }
                                ?.let { it as TotalFuture }
                                ?.searchTotal
                            if (firstSearchTotal != null)
                                transactionsInteractor.importTransactions(
                                    listOf(
                                        Transaction(
                                            date = LocalDate.of(2018, 1, 1),
                                            description = "Mock description of transaction",
                                            amount = firstSearchTotal,
                                            categoryAmounts = mapOf(),
                                            categorizationDate = null,
                                            id = generateUniqueID(),
                                        )
                                    )
                                )
                            else
                                Completable.fromCallable { application.easyToast("No TotalFutures found") }.subscribeOn(AndroidSchedulers.mainThread())
                        }
                        .subscribe()
                },
                MenuVMItem("View TransactionBlock completion") {
                    TransactionBlockCompletionFrag.navTo(nav.value!!)
                },
            )
        }
    }
}