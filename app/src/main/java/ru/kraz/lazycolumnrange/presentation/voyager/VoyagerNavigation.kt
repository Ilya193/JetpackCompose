package ru.kraz.lazycolumnrange.presentation.voyager

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenProvider
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

sealed class SharedScreen : ScreenProvider {
    data object NotesScreen : SharedScreen()
}

class MainScreen : Screen {

    @Composable
    override fun Content() {
        //ru.kraz.lazycolumnrange.presentation.Content()
    }
}

class NotesScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Box(
            modifier = Modifier
                .size(100.dp)
                .clickable {
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "NotesScreen", modifier = Modifier.wrapContentSize())
        }
    }
}