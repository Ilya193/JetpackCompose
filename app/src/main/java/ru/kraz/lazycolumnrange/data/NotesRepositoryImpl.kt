package ru.kraz.lazycolumnrange.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.kraz.lazycolumnrange.data.Mappers.toNoteDb
import ru.kraz.lazycolumnrange.domain.NoteDomain
import ru.kraz.lazycolumnrange.domain.NotesRepository
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val dao: NotesDao,
) : NotesRepository {
    override suspend fun fetchNotes(): Flow<List<NoteDomain>> = dao.fetchNotes().map {
        it.map { it.toNoteDomain() }
    }

    override suspend fun deleteNote(note: NoteDomain) = dao.deleteNote(note.toNoteDb())

    override suspend fun completedNote(note: NoteDomain) = dao.completedNote(note.toNoteDb())
    override suspend fun insertNote(note: NoteDomain) = dao.insertNote(note.toNoteDb())
}