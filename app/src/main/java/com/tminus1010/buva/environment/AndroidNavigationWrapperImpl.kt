package com.tminus1010.buva.environment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.NavController
import com.tminus1010.buva.R
import com.tminus1010.buva.all_layers.KEY1
import com.tminus1010.buva.all_layers.KEY2
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.domain.Transaction
import com.tminus1010.buva.ui.category_details.CategoryDetailsFrag
import com.tminus1010.tmcommonkotlin.androidx.launchOnMainThread
import dagger.Reusable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@Reusable
class AndroidNavigationWrapperImpl @Inject constructor() : AndroidNavigationWrapper {
    private val nav get() = Companion.nav ?: error("This class expects Companion.nav to be assigned")
    override fun navToCreateCategory() = launchOnMainThread {
        CategoryDetailsFrag.navTo(nav, null)
    }

    override fun navToEditCategory(category: Category) = launchOnMainThread {
        CategoryDetailsFrag.navTo(nav, category)
    }

    override fun navToImport() = launchOnMainThread {
        nav.navigate(R.id.importFrag)
    }

    override fun navToCategorize() = launchOnMainThread {
        nav.navigate(R.id.categorizeNestedGraph)
    }

    override fun navToFutures() = launchOnMainThread {
        nav.navigate(R.id.futuresFrag)
    }

    override fun navToTransactions() = launchOnMainThread {
        nav.navigate(R.id.transactionsFrag)
    }

    override fun navToHistory() = launchOnMainThread {
        nav.navigate(R.id.historyFrag)
    }

    override fun navToCreateFuture() = launchOnMainThread {
        nav.navigate(R.id.createFutureFrag)
    }

    override fun navUp() = launchOnMainThread {
        nav.navigateUp()
    }

    override suspend fun navToSetString(s: String): String? {
        return channelFlow { // TODO: This could be simplified.
            launchOnMainThread {
                nav.navigate(
                    R.id.editStringFrag,
                    Bundle().apply {
                        putString(KEY1, s)
                        putParcelable(KEY2, ParcelableLambdaWrapper {
                            GlobalScope.launch { send(it) }
                        })
                    }
                )
            }
            awaitClose()
        }.take(1).first()
    }

    override suspend fun navToChooseTransaction(): Transaction? {
        return channelFlow { // TODO: This could be simplified.
            launchOnMainThread {
                nav.navigate(
                    R.id.chooseTransactionFrag,
                    Bundle().apply {
                        putParcelable(KEY2, ParcelableTransactionLambdaWrapper {
                            GlobalScope.launch { send(it) }
                        })
                    }
                )
            }
            awaitClose()
        }.take(1).first()
    }

    companion object {
        // This pattern can cause memory leaks. However, this project only has 1 Activity, so a memory leak is unlikely
        @SuppressLint("StaticFieldLeak")
        var nav: NavController? = null
    }
}