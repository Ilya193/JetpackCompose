package ru.kraz.lazycolumnrange

import android.app.Application
import cafe.adriel.voyager.core.registry.ScreenRegistry
import dagger.hilt.android.HiltAndroidApp
import ru.kraz.lazycolumnrange.presentation.voyager.NotesScreen
import ru.kraz.lazycolumnrange.presentation.voyager.SharedScreen

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ScreenRegistry {
            register<SharedScreen.NotesScreen> {
                NotesScreen()
            }
        }
    }
}
