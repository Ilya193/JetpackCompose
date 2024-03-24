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
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private var todos = mutableListOf<TodoCloud>()
    private var displayTodos = mutableListOf<TodoCloud>()

    private val _uiState = MutableStateFlow<TodosUiState>(TodosUiState.Loading)
    val uiState: StateFlow<TodosUiState> get() = _uiState.asStateFlow()

    fun fetch() = viewModelScope.launch(dispatcher) {
        viewModelScope.launch(dispatcher) {
            try {
                todos = repository.fetchTodos().toMutableList()
                displayTodos = todos.toMutableList()
                _uiState.value = TodosUiState.Todos(todos.toList())
            } catch (e: Exception) {
                _uiState.value = TodosUiState.Error
            }
        }
    }

    fun action(action: Action) {
        when (action) {
            is Action.SearchMode -> searchMode()
            is Action.InputTodo -> inputTodo(action.todo)
            is Action.ClickTodo -> clickTodo(action.index)
            is Action.FilterMode -> filterMode(action.mode, action.todos, action.filtered)
            is Action.ApplyFilter -> applyFilter(action.filtered)
            is Action.Todos -> todos()
            is Action.TodosWithDetailsTodo -> todosWithDetailsTodo(action.index)
            is Action.CancelDetails -> cancelDetails()
        }
    }

    private fun searchMode() = viewModelScope.launch(dispatcher) {
        _uiState.value = TodosUiState.Search(emptyList(), false)
    }

    private fun inputTodo(todo: String) = viewModelScope.launch(dispatcher) {
        val filteredTodos = mutableListOf<TodoCloud>()
        displayTodos.forEach {
            if (todo in it.title) filteredTodos.add(it)
        }
        _uiState.value =
            TodosUiState.Search(
                todos = filteredTodos.toList(),
                nothingFound = filteredTodos.isEmpty()
            )
    }

    private fun clickTodo(index: Int) = viewModelScope.launch(dispatcher) {
        displayTodos[index] = displayTodos[index].copy(selected = !displayTodos[index].selected)
        _uiState.value = TodosUiState.Todos(displayTodos.toList())
    }

    private fun filterMode(mode: Boolean, todos: List<TodoCloud>, filtered: List<Int>) =
        viewModelScope.launch(dispatcher) {
            _uiState.value =
                if (mode) TodosUiState.Filter(
                    todos = todos.toList(),
                    filtered = filtered,
                    filterMode = true,
                    nothingFound = todos.isEmpty()
                )
                else {
                    if (filtered.isNotEmpty()) {
                        TodosUiState.Filter(
                            todos = todos.toList(),
                            filtered = filtered,
                            filterMode = false,
                            nothingFound = todos.isEmpty()
                        )
                    }
                    else TodosUiState.Todos(todos = displayTodos.toList())
                }
        }

    private fun applyFilter(filtered: List<Int>) = viewModelScope.launch(dispatcher) {
        if (filtered.isEmpty()) {
            _uiState.value = TodosUiState.Todos(todos = displayTodos.toList())
        } else {
            val filter = filtered[0]
            if (filter == 1) {
                _uiState.value = TodosUiState.Filter(
                    todos = displayTodos.toList(),
                    filtered,
                    false,
                    displayTodos.isEmpty()
                )
            } else if (filter == 2) {
                val filteredTodos = mutableListOf<TodoCloud>()
                displayTodos.forEach {
                    if (it.selected) filteredTodos.add(it)
                }
                _uiState.value = TodosUiState.Filter(
                    todos = filteredTodos.toList(),
                    filtered,
                    false,
                    filteredTodos.isEmpty()
                )
            } else {
                val filteredTodos = displayTodos.sortedBy { !it.selected }
                _uiState.value = TodosUiState.Filter(
                    todos = filteredTodos.toList(),
                    filtered,
                    false,
                    filteredTodos.isEmpty()
                )
            }
        }
    }

    private fun todos() = viewModelScope.launch(dispatcher) {
        _uiState.value = TodosUiState.Todos(todos = displayTodos.toList())
    }

    private fun todosWithDetailsTodo(index: Int) = viewModelScope.launch(dispatcher) {
        val data = displayTodos.toList()
        _uiState.value = TodosUiState.TodosWithDetailsTodo(todos = data, todo = data[index])
    }

    private fun cancelDetails() = viewModelScope.launch(dispatcher) {
        _uiState.value = TodosUiState.Todos(todos = displayTodos.toList())
    }
}

sealed interface Action {
    data object SearchMode : Action
    data class InputTodo(val todo: String) : Action
    data class ClickTodo(val index: Int) : Action
    data class FilterMode(
        val mode: Boolean,
        val todos: List<TodoCloud> = emptyList(),
        val filtered: List<Int> = emptyList()
    ) : Action

    data class ApplyFilter(val filtered: List<Int>) : Action
    data object Todos : Action

    data class TodosWithDetailsTodo(val index: Int) : Action
    data object CancelDetails : Action
}

sealed interface TodosUiState {
    data object Loading : TodosUiState

    data object Error : TodosUiState

    data class Todos(
        val todos: List<TodoCloud>
    ) : TodosUiState

    data class TodosWithDetailsTodo(
        val todos: List<TodoCloud>,
        val todo: TodoCloud
    ) : TodosUiState

    data class Search(
        val todos: List<TodoCloud>,
        val nothingFound: Boolean
    ) : TodosUiState

    data class Filter(
        val todos: List<TodoCloud>,
        val filtered: List<Int> = emptyList(),
        val filterMode: Boolean,
        val nothingFound: Boolean
    ) : TodosUiState
}