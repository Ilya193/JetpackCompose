package ru.kraz.lazycolumnrange.di

import android.content.Context
import androidx.room.Room
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kraz.AppDatabase
import ru.kraz.lazycolumnrange.data.NotesRepositoryImpl
import ru.kraz.lazycolumnrange.domain.NotesRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideSqlDriver(@ApplicationContext context: Context): SqlDriver =
        AndroidSqliteDriver(AppDatabase.Schema, context, "notes.db")

    // COMPILE ERROR
    /*@Singleton
    @Provides
    fun provideAppDatabase(sqlDriver: SqlDriver): AppDatabase =
        AppDatabase(sqlDriver) */

    @Provides
    fun provideNotesRepository(sqlDriver: SqlDriver): NotesRepository =
        NotesRepositoryImpl(AppDatabase(sqlDriver))
}