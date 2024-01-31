package ru.kraz.lazycolumnrange

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel : ViewModel() {

    private val messages = mutableListOf<MessageUi>()
    private val _uiState = MutableStateFlow<List<MessageUi>>(emptyList())
    val uiState: StateFlow<List<MessageUi>> get() = _uiState

    fun sendMessage(text: String) = viewModelScope.launch(Dispatchers.IO) {
        messages.add(MessageUi(text = text, iSendThis = Random.nextBoolean()))
        val index = messages.size - 1
        _uiState.value = messages.toList()
        delay(3000)
        readMessage(index)
    }

    private fun readMessage(position: Int) {
        messages[position] = messages[position].copy(messageRead = true)
        _uiState.value = messages.toList()
    }
}

data class MessageUi(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val iSendThis: Boolean,
    val messageRead: Boolean = false
)