package com.tminus1010.buva.all_layers

import android.app.Application
import com.tminus1010.buva.data.SettingsRepo
import com.tminus1010.buva.environment.EnvironmentModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [EnvironmentModule::class])
interface AppComponent {

    fun settingsRepo(): SettingsRepo

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun environmentModule2(environmentModule: EnvironmentModule): Builder
        fun build(): AppComponent
    }
}