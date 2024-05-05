package ru.kraz.lazycolumnrange.domain

import javax.inject.Inject

class CompletedNotesUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(note: NoteDomain) = repository.completedNote(note)
}