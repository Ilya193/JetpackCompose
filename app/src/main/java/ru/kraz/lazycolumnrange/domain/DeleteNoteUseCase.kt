package ru.kraz.lazycolumnrange.domain

class DeleteNoteUseCase(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(note: NoteDomain) = repository.deleteNote(note)
}