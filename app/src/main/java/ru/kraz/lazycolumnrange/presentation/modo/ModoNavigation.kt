package ru.kraz.lazycolumnrange.presentation.modo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.forward
import com.github.terrakok.modo.stack.replace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

sealed interface NavigationCommand {

    data object Init : NavigationCommand
    data object Coup : NavigationCommand
    data class NavigationForward(val screen: Screen) : NavigationCommand
}

fun NavigationContainer<StackState>.launchScreen(command: NavigationCommand) {
    when (command) {
        is NavigationCommand.Init -> {}
        is NavigationCommand.Coup -> {}
        is NavigationCommand.NavigationForward -> forward(command.screen)
    }
}

interface Navigation<T> {
    fun read(): StateFlow<T>
    fun update(value: T)
    fun coup()

    class Base @Inject constructor() : Navigation<NavigationCommand> {
        private val screen = MutableStateFlow<NavigationCommand>(NavigationCommand.Init)

        override fun read(): StateFlow<NavigationCommand> = screen.asStateFlow()

        override fun update(value: NavigationCommand) {
            screen.value = value
        }

        override fun coup() {
            update(NavigationCommand.Coup)
        }
    }
}


class TestViewModel @Inject constructor(
    private val navigation: Navigation<NavigationCommand> = Navigation.Base()
) : ViewModel() {

    init {
        viewModelScope.launch {
            delay(1500)
            openDetails()
        }
    }

    fun openDetails() {
        navigation.update(NavigationCommand.NavigationForward(DetailsScreen(14)))
    }

    fun read(): StateFlow<NavigationCommand> = navigation.read()
    fun coup() = navigation.coup()
}

@Parcelize
class SampleStack(
    private val stackNavModel: StackNavModel
) : StackScreen(stackNavModel) {

    constructor(rootScreen: Screen) : this(StackNavModel(rootScreen))

    @Composable
    override fun Content() {
        val viewModel: TestViewModel = hiltViewModel()
        val commands by viewModel.read().collectAsStateWithLifecycle()

        DisposableEffect(Unit) {
            onDispose {
                viewModel.coup()
            }
        }

        LaunchedEffect(commands) {
            launchScreen(commands)
        }

        TopScreenContent()
    }
}

@Parcelize
class SampleScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: TestViewModel = hiltViewModel()
        ru.kraz.lazycolumnrange.presentation.Content( onLongClick =  {
            viewModel.openDetails()
        })
        /*Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "SampleScreen")
        }*/
    }
}

@Parcelize
class DetailsScreen(
    private val id: Int,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "DetailsScreen $id")
        }
    }
}