package ru.kraz.lazycolumnrange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val items = mutableListOf<ItemUi>()
    private val _uiState = MutableStateFlow<List<ItemUi>>(emptyList())
    val uiState: StateFlow<List<ItemUi>> get() = _uiState

    init {
        (0..50).map {
            items.add(ItemUi(it, "Item $it"))
        }

        _uiState.value = items.toList()
    }

    fun set(position: Int) {
        for (index in 0..position) {
            items[index] = items[index].copy(selected = !items[position].selected)
        }

        _uiState.value = items.toList()
    }

    fun upload(position: Int) = viewModelScope.launch {
        items[position] = items[position].copy(upload = !items[position].upload)
        _uiState.value = items.toList()
        delay(2500)
        items[position] = items[position].copy(upload = !items[position].upload)
        _uiState.value = items.toList()
    }
}

data class ItemUi(
    val id: Int,
    val text: String,
    val selected: Boolean = false,
    val upload: Boolean = false
)