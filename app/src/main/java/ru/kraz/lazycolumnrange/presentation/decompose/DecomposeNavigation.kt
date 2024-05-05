package ru.kraz.lazycolumnrange.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ru.kraz.lazycolumnrange.domain.NotesRepository
import ru.kraz.lazycolumnrange.presentation.Mappers.toNoteUi
import ru.kraz.lazycolumnrange.presentation.NoteUi

interface NotesComponent {
    val state: StateFlow<Model>

    fun onNoteClicked(note: NoteUi)
    fun onNoteDeleted(note: NoteUi)
    fun addNote(title: String)
    fun showDialog(showDialog: Boolean)

    data class Model(
        val items: List<NoteUi> = emptyList(),
        val showDialog: Boolean = false
    )
}

class NotesComponentImpl(
    componentContext: ComponentContext,
    private val repository: NotesRepository
) : NotesComponent {

    private val _state = MutableStateFlow(NotesComponent.Model())
    override val state: StateFlow<NotesComponent.Model> = _state.asStateFlow()

    private val scope = componentContext.coroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        scope.launch {
            repository.fetchNotes().collect {
                _state.value = NotesComponent.Model(it.map { it.toNoteUi() })
            }
        }
    }

    override fun onNoteClicked(note: NoteUi) {
        scope.launch {
            repository.completedNote(note.toNoteDomain())
        }
    }

    override fun onNoteDeleted(note: NoteUi) {
        scope.launch {
            repository.deleteNote(note.toNoteDomain())
        }
    }

    override fun addNote(title: String) {
        scope.launch {
            repository.insertNote(title)
        }
    }

    override fun showDialog(showDialog: Boolean) {
        _state.update { it.copy(showDialog = showDialog) }
    }
}

interface RootComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Notes(val component: NotesComponent) : Child()
    }

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            repository: NotesRepository
        ): RootComponent
    }
}

class RootComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val repository: NotesRepository
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Notes,
            handleBackButton = true,
            childFactory = ::createChild,
        )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): RootComponent.Child =
        when (config) {
            is Config.Notes -> RootComponent.Child.Notes(notes(componentContext))
        }

    private fun notes(componentContext: ComponentContext): NotesComponent =
        NotesComponentImpl(componentContext, repository)

    @Serializable
    private sealed class Config {
        @Serializable
        data object Notes : Config()
    }

    @AssistedFactory
    interface Factory : RootComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            repository: NotesRepository
        ): RootComponentImpl
    }
}