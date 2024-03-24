package ru.kraz.lazycolumnrange

import kotlinx.serialization.Serializable

@Serializable
data class TodoCloud(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean,
    val selected: Boolean = false
)