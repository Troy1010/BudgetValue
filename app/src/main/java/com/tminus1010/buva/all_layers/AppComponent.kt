package com.tminus1010.buva.all_layers

import android.app.Application
import com.tminus1010.buva.app.DatePeriodService
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.CategoryRepo
import com.tminus1010.buva.data.PlansRepo
import com.tminus1010.buva.data.SettingsRepo
import com.tminus1010.buva.environment.EnvironmentModule
import com.tminus1010.buva.environment.adapter.MoshiWithCategoriesProvider
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        EnvironmentModule::class,
    ],
)
interface AppComponent {

    fun settingsRepo(): SettingsRepo
    fun activePlanRepo(): ActivePlanRepo
    fun categoryRepo(): CategoryRepo
    fun activeReconciliationRepo(): ActiveReconciliationRepo
    fun moshiWithCategoriesProvider(): MoshiWithCategoriesProvider
    fun plansRepo(): PlansRepo
    fun datePeriodService(): DatePeriodService

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun environmentModule(environmentModule: EnvironmentModule): Builder
        fun build(): AppComponent
    }
}