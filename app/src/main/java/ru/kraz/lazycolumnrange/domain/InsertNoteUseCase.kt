package ru.kraz.lazycolumnrange.domain

class InsertNoteUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(note: NoteDomain) = repository.insertNote(note)
}