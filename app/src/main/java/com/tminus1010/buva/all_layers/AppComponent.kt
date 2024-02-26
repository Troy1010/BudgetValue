package com.tminus1010.buva.all_layers

import android.app.Application
import com.tminus1010.buva.data.ActivePlanRepo
import com.tminus1010.buva.data.ActiveReconciliationRepo
import com.tminus1010.buva.data.CategoryRepo
import com.tminus1010.buva.data.SettingsRepo
import com.tminus1010.buva.environment.EnvironmentModule
import com.tminus1010.buva.environment.EnvironmentModule_NotCurrentlyReplacedInUnitTests
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        EnvironmentModule::class,
        EnvironmentModule_NotCurrentlyReplacedInUnitTests::class,
    ],
)
interface AppComponent {

    fun settingsRepo(): SettingsRepo
    fun activePlanRepo(): ActivePlanRepo
    fun categoryRepo(): CategoryRepo
    fun activeReconciliationRepo(): ActiveReconciliationRepo

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun environmentModule(environmentModule: EnvironmentModule): Builder
        fun build(): AppComponent
    }
}