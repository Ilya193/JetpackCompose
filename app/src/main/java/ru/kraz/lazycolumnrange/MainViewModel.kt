package ru.kraz.lazycolumnrange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val notes = mutableListOf<NoteUi>()

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> get() = _uiState.asStateFlow()

    fun action(event: Event) = viewModelScope.launch(dispatcher) {
        when (event) {
            is Event.ShowDialog -> showDialog(event.showDialog)
            is Event.AddNote -> addNote(event.title)
            is Event.DeleteNote -> deleteNote(event.note)
        }
    }

    private fun showDialog(showDialog: Boolean) {
        _uiState.update {
            it.copy(showDialog = showDialog)
        }
    }

    private fun addNote(title: String) {
        notes.add(NoteUi(id = notes.size - 1, title = title))
        _uiState.value = NotesUiState(notes = notes.toList())
    }

    private fun deleteNote(note: NoteUi) {
        notes.remove(note)
        _uiState.value = NotesUiState(notes = notes.toList())
    }
}

sealed interface Event {
    data class ShowDialog(val showDialog: Boolean) : Event
    data class AddNote(val title: String) : Event
    data class DeleteNote(val note: NoteUi) : Event
}

data class NotesUiState(
    val notes: List<NoteUi> = emptyList(),
    val showDialog: Boolean = false
)

data class NoteUi(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false
)