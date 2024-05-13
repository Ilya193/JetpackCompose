package ru.kraz.lazycolumnrange.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.kraz.lazycolumnrange.data.NotesDao
import ru.kraz.lazycolumnrange.data.NotesRepositoryImpl
import ru.kraz.lazycolumnrange.domain.NotesRepository

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideNotesRepository(dao: NotesDao): NotesRepository = NotesRepositoryImpl(dao)
}