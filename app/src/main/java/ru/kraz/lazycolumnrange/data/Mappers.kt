package ru.kraz.lazycolumnrange.data

import ru.kraz.lazycolumnrange.domain.NoteDomain

object Mappers {
    fun NoteDomain.toNoteDb(): NoteDb = NoteDb(id, title, isCompleted)
}