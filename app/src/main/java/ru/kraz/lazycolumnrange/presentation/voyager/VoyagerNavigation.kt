package ru.kraz.lazycolumnrange.presentation.voyager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Immutable
data class CommentUi(
    val id: Int,
    val comment: String,
    val date: String
)

data class DetailsUiState(
    val comment: CommentUi? = null,
    val isSubscribed: Boolean = false
)

class DetailsScreenModel(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : StateScreenModel<DetailsUiState>(DetailsUiState()) {

    private val sdf = SimpleDateFormat("dd/M/yyyy HH:MM:ss", Locale.getDefault())
    init {
        screenModelScope.launch(dispatcher) {
            mutableState.value = DetailsUiState(CommentUi(0, "Comment", sdf.format(Date())))
        }
    }

    fun subscribe() {
        screenModelScope.launch(dispatcher) {
            mutableState.update { it.copy(isSubscribed = !it.isSubscribed) }
        }
    }

    fun changeComment() {
        screenModelScope.launch(dispatcher) {
            mutableState.update { it.copy(comment = CommentUi(0, "New comment", sdf.format(Date()))) }
        }
    }
}

class ListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "ListScreen",
                modifier = Modifier
                    .clickable { navigator.push(DetailsScreen()) }
                    .align(Alignment.Center)
            )
        }
    }

}

class DetailsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { DetailsScreenModel() }
        val state by screenModel.state.collectAsStateWithLifecycle()

        Column(modifier = Modifier.fillMaxSize()) {
            state.comment?.let {
                CommentItem(comment = it, onClick = remember {
                    screenModel::changeComment
                })
            }
            Button(onClick = screenModel::subscribe) {
                if (state.isSubscribed)
                    Text(text = "Вы уже подписаны")
                else
                    Text(text = "Подписаться")
            }
        }
    }
}

@Composable
fun CommentItem(comment: CommentUi, onClick: () -> Unit) {
    Text(text = comment.id.toString())
    Text(text = comment.comment)
    Text(text = comment.date, modifier = Modifier.clickable { onClick() })
}

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "ProfileScreen")
        }
    }
}

object TabList : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Home)
            val title = "Home"
            val index: UShort = 0u
            return TabOptions(index, title, icon)
        }

    @Composable
    override fun Content() {
        Navigator(ListScreen())
    }
}

object TabProfile : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Person)
            val title = "Profile"
            val index: UShort = 1u
            return TabOptions(index, title, icon)
        }

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "ProfileScreen")
        }
    }
}