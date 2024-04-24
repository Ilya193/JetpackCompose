package ru.kraz.lazycolumnrange

import android.app.Application
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
        }
    }
}

val appModule = module {
    viewModel<MainViewModel> {
        MainViewModel()
    }
}