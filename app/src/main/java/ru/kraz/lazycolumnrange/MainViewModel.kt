package ru.kraz.lazycolumnrange

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flow

class MainViewModel : ViewModel() {

    val words = flow {
        emit(
            listOf(
                WordUi(
                    0,
                    listOf(
                        LettersMode.Basis("справ", "к"),
                        LettersMode.Ending("а")
                    )
                ),
                WordUi(
                    1,
                    listOf(
                        LettersMode.Basis("розетк"),
                        LettersMode.Ending("а")
                    )
                ),
                WordUi(
                    2,
                    listOf(
                        LettersMode.Basis("клави", "ат ур"),
                        LettersMode.Ending("а")
                    )
                )
            )
        )
    }

}

sealed interface LettersMode {

    data class Basis(val root: String, val suffix: String? = null) : LettersMode
    data class Ending(val letters: String) : LettersMode
}

data class WordUi(val id: Int, val letters: List<LettersMode>)