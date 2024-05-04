package ru.kraz.lazycolumnrange.domain

import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(note: NoteDomain) = repository.deleteNote(note)
}