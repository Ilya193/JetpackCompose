package ru.kraz.lazycolumnrange.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ru.kraz.lazycolumnrange.data.NotesDao
import ru.kraz.lazycolumnrange.data.NotesRepositoryImpl
import ru.kraz.lazycolumnrange.domain.CompletedNoteUseCase
import ru.kraz.lazycolumnrange.domain.DeleteNoteUseCase
import ru.kraz.lazycolumnrange.domain.FetchNotesUseCase
import ru.kraz.lazycolumnrange.domain.InsertNoteUseCase
import ru.kraz.lazycolumnrange.domain.NotesRepository

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {
    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun provideNotesRepository(dao: NotesDao): NotesRepository = NotesRepositoryImpl(dao)

    @Provides
    fun provideFetchNotesUseCase(repository: NotesRepository): FetchNotesUseCase =
        FetchNotesUseCase(repository)

    @Provides
    fun provideInsertNoteUseCase(repository: NotesRepository): InsertNoteUseCase =
        InsertNoteUseCase(repository)

    @Provides
    fun provideDeleteNoteUseCase(repository: NotesRepository): DeleteNoteUseCase =
        DeleteNoteUseCase(repository)

    @Provides
    fun provideCompletedNoteUseCase(repository: NotesRepository): CompletedNoteUseCase =
        CompletedNoteUseCase(repository)
}