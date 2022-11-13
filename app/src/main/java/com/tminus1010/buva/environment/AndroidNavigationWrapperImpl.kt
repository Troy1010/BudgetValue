package com.tminus1010.buva.environment

import android.annotation.SuppressLint
import androidx.navigation.NavController
import com.tminus1010.buva.domain.Category
import com.tminus1010.buva.ui.category_details.CategoryDetailsFrag
import com.tminus1010.buva.ui.set_string.SetStringFrag
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

    override fun navUp() = launchOnMainThread {
        nav.navigateUp()
    }

    override suspend fun navToSetString(s: String): String? {
        return channelFlow { // TODO: This could be simplified.
            launchOnMainThread {
                SetStringFrag.navTo(nav, s, ParcelableLambdaWrapper {
                    GlobalScope.launch { send(it) }
                })
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