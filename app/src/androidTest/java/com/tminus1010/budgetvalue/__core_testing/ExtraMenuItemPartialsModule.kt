package com.tminus1010.budgetvalue.__core_testing

import android.app.Application
import com.tminus1010.budgetvalue._core.presentation.model.MenuVMItem
import com.tminus1010.budgetvalue._core.presentation.service.GetExtraMenuItemPartials
import com.tminus1010.budgetvalue.app_init.AppInitRepo
import com.tminus1010.budgetvalue.app_init.AppInteractor
import com.tminus1010.budgetvalue.replay_or_future.data.FuturesRepo
import com.tminus1010.budgetvalue.replay_or_future.domain.TerminationStatus
import com.tminus1010.budgetvalue.replay_or_future.domain.TotalFuture
import com.tminus1010.budgetvalue.transactions.app.Transaction
import com.tminus1010.budgetvalue.transactions.app.interactor.TransactionsInteractor
import com.tminus1010.tmcommonkotlin.misc.generateUniqueID
import com.tminus1010.tmcommonkotlin.rx.extensions.toSingle
import com.tminus1010.tmcommonkotlin.view.extensions.easyToast
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import java.time.LocalDate
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ExtraMenuItemPartialsModule {
    @Provides
    @Singleton
    fun getExtraMenuItemPartials(appInitRepo: AppInitRepo, appInteractor: AppInteractor, transactionsInteractor: TransactionsInteractor, futuresRepo: FuturesRepo, application: Application) = object : GetExtraMenuItemPartials() {
        override fun invoke(): Array<MenuVMItem> {
            return arrayOf(
                MenuVMItem("Redo App Init") {
                    appInitRepo.pushAppInitBool(false)
                        .andThen(appInteractor)
                        .subscribe()
                },
                MenuVMItem("Import Transaction for Future") {
                    futuresRepo.fetchFutures().toSingle()
                        .flatMapCompletable { futures ->
                            val firstSearchTotal = futures
                                .find { it is TotalFuture && it.terminationStatus == TerminationStatus.WAITING_FOR_MATCH }
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
            )
        }
    }
}