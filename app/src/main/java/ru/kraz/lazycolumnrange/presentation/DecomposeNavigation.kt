package ru.kraz.lazycolumnrange.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

class ListComponent(
    componentContext: ComponentContext,
    private val click: () -> Unit,
) : ComponentContext by componentContext {

    fun onClick() {
        click()
    }
}

class DetailsComponent(
    componentContext: ComponentContext,
    private val back: () -> Unit
) : ComponentContext by componentContext {

    fun pop() {
        back()
    }
}

class ProductsComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Products>()

    val childStack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Products.serializer(),
            initialConfiguration = Products.List,
            handleBackButton = true,
            childFactory = ::createStack,
        )

    private fun createStack(
        config: Products,
        componentContext: ComponentContext
    ): Child =
        when (config) {
            is Products.List -> Child.List(ListComponent(componentContext, click = {
                navigation.push(Products.Details)
            }))

            is Products.Details -> Child.Details(DetailsComponent(componentContext, back = {
                navigation.pop()
            }))
        }

    sealed class Child {
        data class List(val component: ListComponent) : Child()
        data class Details(val component: DetailsComponent) : Child()
    }

    @Serializable
    sealed class Products {
        @Serializable
        data object List : Products()

        @Serializable
        data object Details : Products()
    }

}

class BasketComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {}

class ProfileComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {}

class MenuComponent(
    componentContext: ComponentContext,
    private val onClick: (DefaultRootComponent.TopConfig) -> Unit
) : ComponentContext by componentContext {

    fun onItemClicked(item: Int) {
        when (item) {
            1 -> onClick(DefaultRootComponent.TopConfig.Products)
            2 -> onClick(DefaultRootComponent.TopConfig.Basket)
            3 -> onClick(DefaultRootComponent.TopConfig.Profile)
        }
    }
}

interface RootComponent {

    val childStack: Value<ChildStack<*, Child>>
    val bottomStack: Value<ChildStack<*, Bottom>>

    sealed class Child {
        data class Products(val component: ProductsComponent) : Child()
        data class Basket(val component: BasketComponent) : Child()
        data class Profile(val component: ProfileComponent) : Child()
    }

    sealed class Bottom {
        data class Menu(val component: MenuComponent) : Bottom()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private val topNavigation = StackNavigation<TopConfig>()

    private val products = ProductsComponent(componentContext)

    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = topNavigation,
            serializer = TopConfig.serializer(),
            key = "ChildStack",
            initialConfiguration = TopConfig.Products,
            handleBackButton = true,
            childFactory = ::createChildStack,
        )

    private fun createChildStack(
        config: TopConfig,
        componentContext: ComponentContext
    ): RootComponent.Child =
        when (config) {
            is TopConfig.Products -> RootComponent.Child.Products(products)
            is TopConfig.Basket -> RootComponent.Child.Basket(BasketComponent(componentContext))
            is TopConfig.Profile -> RootComponent.Child.Profile(ProfileComponent(componentContext))
        }

    private val bottomNavigation = StackNavigation<BottomConfig>()

    override val bottomStack: Value<ChildStack<*, RootComponent.Bottom>> =
        childStack(
            source = bottomNavigation,
            serializer = BottomConfig.serializer(),
            key = "BottomStack",
            initialConfiguration = BottomConfig.Menu,
            handleBackButton = false,
            childFactory = ::createBottomStack,
        )

    private fun createBottomStack(
        config: BottomConfig,
        componentContext: ComponentContext
    ): RootComponent.Bottom =
        when (config) {
            is BottomConfig.Menu -> RootComponent.Bottom.Menu(
                MenuComponent(
                    componentContext,
                    onClick = {
                        topNavigation.replaceAll(it)
                    })
            )
        }

    @Serializable
    sealed class TopConfig {
        @Serializable
        data object Products : TopConfig()

        @Serializable
        data object Basket : TopConfig()

        @Serializable
        data object Profile : TopConfig()
    }

    @Serializable
    private sealed class BottomConfig {
        @Serializable
        data object Menu : BottomConfig()
    }
}