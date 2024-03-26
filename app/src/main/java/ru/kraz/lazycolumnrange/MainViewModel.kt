package ru.kraz.lazycolumnrange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: Repository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private var todos = mutableListOf<TodoCloud>()
    private var displayTodos = mutableListOf<TodoCloud>()

    private val _uiState = MutableStateFlow<TodosUiState>(TodosUiState(isLoading = true))
    val uiState: StateFlow<TodosUiState> get() = _uiState.asStateFlow()

    fun fetch() = viewModelScope.launch(dispatcher) {
        viewModelScope.launch(dispatcher) {
            try {
                todos = repository.fetchTodos().toMutableList()
                displayTodos = todos.toMutableList()
                _uiState.value = TodosUiState(todos = displayTodos.toList())
            } catch (e: Exception) {
                _uiState.value = TodosUiState(isError = true)
            }
        }
    }

    fun action(action: Action) {
        when (action) {
            is Action.SearchMode -> searchMode()
            is Action.InputTodo -> inputTodo(action.todo)
            is Action.ClickTodo -> clickTodo(action.index)
            is Action.FilterMode -> filterMode(action.mode, action.filtered)
            is Action.ApplyFilter -> applyFilter(action.filtered)
            is Action.Todos -> todos()
            is Action.TodosWithDetailsTodo -> todosWithDetailsTodo(action.index)
            is Action.CancelDetails -> cancelDetails()
        }
    }

    private fun searchMode() = viewModelScope.launch(dispatcher) {
        _uiState.value = TodosUiState(mode = Mode.Search)
    }

    private fun inputTodo(todo: String) = viewModelScope.launch(dispatcher) {
        _uiState.update {
            if (todo.isEmpty())
                it.copy(
                    todos = emptyList(),
                    nothingFound = NothingFound.Init
                )
            else {
                val filteredTodos = mutableListOf<TodoCloud>()
                displayTodos.forEach {
                    if (todo in it.title) filteredTodos.add(it)
                }

                it.copy(
                    todos = filteredTodos.toList(),
                    nothingFound = if (filteredTodos.isEmpty()) NothingFound.Search else NothingFound.Init
                )
            }
        }
    }

    private fun clickTodo(index: Int) = viewModelScope.launch(dispatcher) {
        val item = displayTodos[index]
        displayTodos[index] = item.copy(selected = !item.selected)
        _uiState.update { it.copy(todos = displayTodos.toList()) }
    }

    private fun filterMode(show: Boolean, filtered: List<Int>) {
        _uiState.update {
            if (show) {
                it.copy(
                    mode = Mode.Filter(true),
                    filtered = filtered,
                    nothingFound = if (it.todos.isEmpty()) NothingFound.Filter else NothingFound.Init
                )
            } else {
                if (filtered.isNotEmpty())
                    it.copy(
                        mode = Mode.Filter(false),
                        filtered = filtered,
                        nothingFound = if (it.todos.isEmpty()) NothingFound.Filter else NothingFound.Init
                    )
                else {
                    TodosUiState(todos = displayTodos.toList())
                }
            }
        }
    }

    private fun applyFilter(filtered: List<Int>) = viewModelScope.launch(dispatcher) {
        if (filtered.isEmpty()) {
            _uiState.value = TodosUiState(todos = displayTodos.toList())
        }
        else {
            var todos = mutableListOf<TodoCloud>()
            when (filtered[0]) {
                1 -> todos = displayTodos.toMutableList()
                2 -> {
                    displayTodos.forEach {
                        if (it.selected) todos.add(it)
                    }
                }
                else -> todos = displayTodos.sortedBy { !it.selected }.toMutableList()
            }
            _uiState.value = TodosUiState(
                todos = todos,
                filtered = filtered,
                mode = Mode.Filter(false),
                nothingFound = if (todos.isEmpty()) NothingFound.Filter else NothingFound.Init
            )
        }
    }

    private fun todos() = viewModelScope.launch {
        _uiState.value = TodosUiState(todos = displayTodos.toList())
    }

    private fun todosWithDetailsTodo(index: Int) = viewModelScope.launch(dispatcher) {
        val data = displayTodos.toList()
        _uiState.value = TodosUiState(todos = data, detailsTodo = data[index])
    }

    private fun cancelDetails() = viewModelScope.launch(dispatcher) {
        _uiState.value = TodosUiState(todos = displayTodos.toList())
    }
}

data class TodosUiState(
    val todos: List<TodoCloud> = emptyList(),
    val todo: String = "",
    val mode: Mode = Mode.Init,
    val detailsTodo: TodoCloud? = null,
    val filtered: List<Int> = emptyList(),
    val nothingFound: NothingFound = NothingFound.Init,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
)

sealed interface Mode {
    data object Search : Mode
    data class Filter(val show: Boolean) : Mode
    data object Init : Mode
}

sealed interface NothingFound {
    data object Search : NothingFound
    data object Filter : NothingFound
    data object Init : NothingFound
}

sealed interface Action {
    data object SearchMode : Action
    data class InputTodo(val todo: String) : Action
    data class ClickTodo(val index: Int) : Action
    data class FilterMode(
        val mode: Boolean,
        val todos: List<TodoCloud> = emptyList(),
        val filtered: List<Int> = emptyList(),
    ) : Action

    data class ApplyFilter(val filtered: List<Int>) : Action
    data object Todos : Action

    data class TodosWithDetailsTodo(val index: Int) : Action
    data object CancelDetails : Action
}