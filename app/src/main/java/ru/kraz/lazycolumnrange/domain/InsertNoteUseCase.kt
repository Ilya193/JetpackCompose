package ru.kraz.lazycolumnrange.domain

import javax.inject.Inject

class InsertNoteUseCase @Inject constructor(
    private val repository: NotesRepository
) {

    suspend operator fun invoke(title: String) = repository.insertNote(title)
}