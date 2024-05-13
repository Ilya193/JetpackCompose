package ru.kraz.lazycolumnrange.domain

data class NoteDomain(
    val id: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val counter: Long?
)