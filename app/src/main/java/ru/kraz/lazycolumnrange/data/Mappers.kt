package ru.kraz.lazycolumnrange.data

import ru.kraz.lazycolumnrange.Note
import ru.kraz.lazycolumnrange.domain.NoteDomain

fun Note.toNoteDomain(): NoteDomain =
    NoteDomain(id, title, isCompleted)