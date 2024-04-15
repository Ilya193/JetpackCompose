package ru.kraz.lazycolumnrange.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.kraz.lazycolumnrange.data.NotesDao
import ru.kraz.lazycolumnrange.data.NotesDb
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideNotesDatabase(@ApplicationContext context: Context): NotesDb =
        Room.databaseBuilder(context, NotesDb::class.java, "notes.db").build()

    @Singleton
    @Provides
    fun provideNotesDao(db: NotesDb): NotesDao = db.notesDao()
}