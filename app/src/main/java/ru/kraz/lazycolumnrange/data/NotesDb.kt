package ru.kraz.lazycolumnrange.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NoteDb::class], version = 1)
abstract class NotesDb : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}