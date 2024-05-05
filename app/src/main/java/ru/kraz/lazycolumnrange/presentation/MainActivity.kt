package ru.kraz.lazycolumnrange.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import dagger.hilt.android.AndroidEntryPoint
import ru.kraz.lazycolumnrange.di.DaggerAppComponent
import ru.kraz.lazycolumnrange.domain.NotesRepository
import ru.kraz.lazycolumnrange.presentation.decompose.Notes
import ru.kraz.lazycolumnrange.presentation.decompose.RootComponent
import ru.kraz.lazycolumnrange.presentation.ui.theme.LazyColumnRangeTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: NotesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            LazyColumnRangeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val root = DaggerAppComponent.create().rootComponent(
                        defaultComponentContext(),
                        repository
                    )
                    Children(
                        stack = root.childStack,
                        modifier = Modifier
                            .fillMaxSize()
                            .systemBarsPadding(),
                        animation = stackAnimation(slide())
                    ) {
                        when (val instance = it.instance) {
                            is RootComponent.Child.Notes -> Notes(component = instance.component)
                        }
                    }
                }
            }
        }
    }
}