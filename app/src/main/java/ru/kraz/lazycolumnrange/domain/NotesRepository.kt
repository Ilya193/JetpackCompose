package ru.kraz.lazycolumnrange.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    suspend fun fetchNotes(): Flow<List<NoteDomain>>
    suspend fun deleteNote(note: NoteDomain)
    suspend fun completedNote(note: NoteDomain)
    suspend fun insertNote(note: NoteDomain)
}