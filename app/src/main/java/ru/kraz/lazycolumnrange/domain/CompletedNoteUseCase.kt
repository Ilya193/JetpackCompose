package ru.kraz.lazycolumnrange.domain

class CompletedNoteUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(note: NoteDomain) = repository.completedNote(note)
}