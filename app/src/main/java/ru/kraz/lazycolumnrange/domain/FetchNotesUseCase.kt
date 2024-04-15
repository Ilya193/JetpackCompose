package ru.kraz.lazycolumnrange.domain

import kotlinx.coroutines.flow.Flow

class FetchNotesUseCase(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(): Flow<List<NoteDomain>> = repository.fetchNotes()
}