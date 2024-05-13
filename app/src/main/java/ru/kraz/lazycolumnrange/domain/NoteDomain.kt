package ru.kraz.lazycolumnrange.domain

data class NoteDomain(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false,
    val counter: Int? = null
)