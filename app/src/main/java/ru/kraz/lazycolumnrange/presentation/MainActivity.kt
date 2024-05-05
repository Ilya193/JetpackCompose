package ru.kraz.lazycolumnrange.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ru.kraz.lazycolumnrange.presentation.ui.theme.LazyColumnRangeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            LazyColumnRangeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content()
                }
            }
        }
    }
}

sealed class BottomScreen(val route: String) {
    data object Products : BottomScreen("PRODUCTS")
    data object Profile : BottomScreen("PROFILE")
}

sealed class ProductsScreen(val route: String) {
    data object List : ProductsScreen("LIST")
    data object Details : ProductsScreen("Details")
}

@Composable
fun Content(
    items: List<BottomScreen> = remember {
        listOf(
            BottomScreen.Products,
            BottomScreen.Profile,
        )
    }
) {
    val bottomNavController = rememberNavController()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            modifier = Modifier.padding(padding),
            navController = bottomNavController,
            startDestination = BottomScreen.Products.route,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            composable(BottomScreen.Products.route) {
                val containerProductsController = rememberNavController()
                val interactionSource = remember { MutableInteractionSource() }

                NavHost(
                    navController = containerProductsController,
                    startDestination = ProductsScreen.List.route,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                ) {
                    composable(ProductsScreen.List.route) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red)
                            .clickable(interactionSource = interactionSource, indication = null) {
                                containerProductsController.navigate(ProductsScreen.Details.route)
                            }, contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Products")
                        }
                    }

                    composable(ProductsScreen.Details.route) {
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Yellow)
                            .clickable(interactionSource = interactionSource, indication = null) {
                                containerProductsController.popBackStack()
                            }, contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Details")
                        }
                    }
                }
            }

            composable(BottomScreen.Profile.route) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Profile")
                }
            }
        }
    }

}