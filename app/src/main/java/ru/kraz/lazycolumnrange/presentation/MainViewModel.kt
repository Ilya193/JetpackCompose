package ru.kraz.lazycolumnrange.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.kraz.lazycolumnrange.domain.CompletedNotesUseCase
import ru.kraz.lazycolumnrange.domain.DeleteNotesUseCase
import ru.kraz.lazycolumnrange.domain.FetchNotesUseCase
import ru.kraz.lazycolumnrange.domain.InsertNotesUseCase
import ru.kraz.lazycolumnrange.presentation.Mappers.toNoteUi
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fetchNotesUseCase: FetchNotesUseCase,
    private val insertNotesUseCase: InsertNotesUseCase,
    private val deleteNotesUseCase: DeleteNotesUseCase,
    private val completedNoteUseCase: CompletedNotesUseCase,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch(dispatcher) {
            fetchNotesUseCase().collect {
                _uiState.value = NotesUiState(it.map { it.toNoteUi() })
            }
        }
    }

    fun action(event: Event) = viewModelScope.launch(dispatcher) {
        when (event) {
            is Event.ShowDialog -> showDialog(event.showDialog)
            is Event.AddNote -> addNote(event.title)
            is Event.DeleteNote -> deleteNote(event.note)
            is Event.CompletedNote -> completedNote(event.note)
        }
    }

    private fun showDialog(showDialog: Boolean) {
        _uiState.update {
            it.copy(showDialog = showDialog)
        }
    }

    private suspend fun addNote(title: String) {
        insertNotesUseCase(title)
    }

    private suspend fun deleteNote(note: NoteUi) {
        deleteNotesUseCase(note.toNoteDomain())
    }

    private suspend fun completedNote(note: NoteUi) {
        completedNoteUseCase(note.copy(isCompleted = !note.isCompleted).toNoteDomain())
    }
}

sealed interface Event {
    data class ShowDialog(val showDialog: Boolean) : Event
    data class AddNote(val title: String) : Event
    data class DeleteNote(val note: NoteUi) : Event
    data class CompletedNote(val note: NoteUi) : Event
}

data class NotesUiState(
    val notes: List<NoteUi> = emptyList(),
    val showDialog: Boolean = false
)

