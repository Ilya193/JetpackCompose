package ru.kraz.lazycolumnrange.presentation

import ru.kraz.lazycolumnrange.domain.NoteDomain

data class NoteUi(
    val id: Long,
    val title: String,
    val isCompleted: Boolean = false
) {
    fun toNoteDomain(): NoteDomain = NoteDomain(id, title, isCompleted)
}