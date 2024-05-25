package ru.kraz.lazycolumnrange.presentation.voyager

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "DetailsScreen")
        }
    }
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