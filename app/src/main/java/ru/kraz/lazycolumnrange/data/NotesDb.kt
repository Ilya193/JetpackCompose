package ru.kraz.lazycolumnrange.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [NoteDb::class],
    version = 2
)
abstract class NotesDb : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}

internal val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notes ADD COLUMN counter INTEGER")
    }
}