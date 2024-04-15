package ru.kraz.lazycolumnrange.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.kraz.lazycolumnrange.domain.NoteDomain

@Entity(tableName = "notes")
data class NoteDb(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false
) {
    fun toNoteDomain(): NoteDomain = NoteDomain(id, title, isCompleted)
}