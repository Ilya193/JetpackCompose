package ru.kraz.lazycolumnrange.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import ru.kraz.AppDatabase
import ru.kraz.lazycolumnrange.domain.NoteDomain
import ru.kraz.lazycolumnrange.domain.NotesRepository
import javax.inject.Inject

class NotesRepositoryImpl(
    private val database: AppDatabase
) : NotesRepository {

    override suspend fun fetchNotes(): Flow<List<NoteDomain>> = flow {
        database.appDatabaseQueries.selectAll().asFlow().mapToList(Dispatchers.IO).collect {
            emit(it.map { it.toNoteDomain() })
        }
    }

    override suspend fun deleteNote(note: NoteDomain) {
        database.appDatabaseQueries.deleteById(note.id)
    }

    override suspend fun completedNote(note: NoteDomain) {
        database.appDatabaseQueries.statistic(if (note.counter == null) 1 else note.counter + 1, note.id)
        database.appDatabaseQueries.competed(note.id)
    }

    override suspend fun insertNote(title: String) {
        database.appDatabaseQueries.insert(title)
    }

}