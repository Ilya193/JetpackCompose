package ru.kraz.lazycolumnrange.domain

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchNotesUseCase @Inject constructor(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(): Flow<List<NoteDomain>> = repository.fetchNotes()
}