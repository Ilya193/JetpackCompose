package ru.kraz.lazycolumnrange.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes")
    fun fetchNotes(): Flow<List<NoteDb>>

    @Delete
    suspend fun deleteNote(note: NoteDb)

    @Update
    suspend fun completedNote(note: NoteDb)

    @Insert
    suspend fun insertNote(note: NoteDb)
}