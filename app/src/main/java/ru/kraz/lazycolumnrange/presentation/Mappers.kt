package ru.kraz.lazycolumnrange.presentation

import ru.kraz.lazycolumnrange.domain.NoteDomain

object Mappers {
    fun NoteDomain.toNoteUi(): NoteUi = NoteUi(id, title, isCompleted, counter)
}