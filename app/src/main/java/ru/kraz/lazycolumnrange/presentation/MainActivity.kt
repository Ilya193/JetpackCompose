package ru.kraz.lazycolumnrange.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import ru.kraz.lazycolumnrange.R
import ru.kraz.lazycolumnrange.presentation.RootComponent.Child
import ru.kraz.lazycolumnrange.presentation.ui.theme.LazyColumnRangeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val root = DefaultRootComponent(defaultComponentContext())
        setContent {
            LazyColumnRangeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.systemBarsPadding(),
                        bottomBar = {
                            Children(
                                stack = root.bottomStack,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                when (val instance = it.instance) {
                                    is RootComponent.Bottom.Menu -> {
                                        BottomContainer(instance.component)
                                    }
                                }
                            }
                        }
                    ) { padding ->
                        Content(component = root, modifier = Modifier.padding(padding))
                    }
                }
            }
        }
    }
}

@Composable
fun Content(component: RootComponent, modifier: Modifier) {
    Children(
        stack = component.childStack,
        modifier = modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (val instance = it.instance) {
                is Child.Products -> {
                    Children(
                        stack = instance.component.childStack,
                        modifier = Modifier
                            .fillMaxSize(),
                        animation = stackAnimation(slide())
                    ) {
                        when (val container = it.instance) {
                            is ProductsComponent.Child.List -> Text(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Red)
                                    .clickable {
                                        container.component.onClick()
                                    }, text = "Products"
                            )

                            is ProductsComponent.Child.Details -> Text(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Yellow)
                                    .clickable {
                                        container.component.pop()
                                    },
                                text = "Details"
                            )
                        }
                    }
                }

                is Child.Basket -> Text(text = "Basket")
                is Child.Profile -> Text(text = "Profile")
            }
        }
    }
}

@Composable
fun BottomContainer(component: MenuComponent) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .clickable { component.onItemClicked(1) },
            painter = painterResource(id = R.drawable.ic_clear),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .clickable { component.onItemClicked(2) },
            painter = painterResource(id = R.drawable.ic_clear),
            contentDescription = null
        )
        Image(
            modifier = Modifier
                .clickable { component.onItemClicked(3) },
            painter = painterResource(id = R.drawable.ic_clear),
            contentDescription = null
        )
    }
}