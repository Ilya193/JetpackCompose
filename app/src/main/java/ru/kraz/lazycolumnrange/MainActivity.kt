package ru.kraz.lazycolumnrange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import ru.kraz.lazycolumnrange.ui.theme.LazyColumnRangeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(viewModel: MainViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetch()
    }

    when (val state = uiState) {
        is TodosUiState.Loading -> {
            LoadingContent()
        }

        is TodosUiState.Error -> {
            ErrorContent { viewModel.fetch() }
        }

        is TodosUiState.Todos -> {
            Scaffold(
                topBar = {
                    TopAppBar(navigationIcon = {
                        Image(
                            modifier = Modifier.clickable {
                                viewModel.action(Action.FilterMode(true, state.todos))
                            },
                            painter = painterResource(id = R.drawable.filter_list),
                            contentDescription = null
                        )
                    }, title = {
                        Text(text = "TopAppBar")
                    }, actions = {
                        Image(modifier = Modifier.clickable {
                            viewModel.action(Action.SearchMode)
                        }, imageVector = Icons.Filled.Search, contentDescription = null)
                    })
                }
            ) { padding ->
                LazyColumn(modifier = Modifier.padding(padding)) {
                    itemsIndexed(
                        items = state.todos,
                        key = { index, item -> item.id }) { index, item ->
                        TodoItem(item) {
                            viewModel.action(Action.ClickTodo(index))
                        }
                    }
                }
            }
        }

        is TodosUiState.Filter -> {
            Scaffold(
                topBar = {
                    TopAppBar(navigationIcon = {
                        Image(
                            modifier = Modifier.clickable {
                                viewModel.action(
                                    Action.FilterMode(
                                        true,
                                        state.todos,
                                        state.filtered
                                    )
                                )
                            },
                            painter = painterResource(id = R.drawable.filter_list),
                            contentDescription = null
                        )
                    }, title = {
                        Text(text = "TopAppBar")
                    }, actions = {
                        Image(modifier = Modifier.clickable {
                            viewModel.action(Action.SearchMode)
                        }, imageVector = Icons.Filled.Search, contentDescription = null)
                    })
                }
            ) { padding ->
                if (state.nothingFound) {
                    InformationText(text = stringResource(R.string.nothing_found))
                } else {
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        itemsIndexed(
                            items = state.todos,
                            key = { index, item -> item.id }) { index, item ->
                            TodoItem(item) {}
                        }
                    }
                }

                if (state.filterMode) {
                    ModalBottomSheet(onDismissRequest = { viewModel.action(Action.FilterMode(false)) }) {
                        BottomSheet(state.filtered) {
                            viewModel.action(Action.ApplyFilter(it))
                        }
                    }
                }
            }
        }

        is TodosUiState.Search -> {
            Scaffold(
                topBar = {
                    TopAppBar(navigationIcon = {
                        Image(modifier = Modifier.clickable {
                            viewModel.action(Action.Todos)
                        }, imageVector = Icons.Filled.ArrowBack, contentDescription = null)
                    }, title = {
                        var todo by remember { mutableStateOf("") }
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = todo,
                            onValueChange = { value ->
                                todo = value
                                viewModel.action(Action.InputTodo(value))
                            },
                            placeholder = { Text(text = stringResource(R.string.todo)) },
                            shape = RoundedCornerShape(0.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                            ),
                        )
                    })
                }
            ) { padding ->
                if (state.nothingFound) {
                    InformationText(text = stringResource(R.string.nothing_found))
                } else if (state.todos.isEmpty()) {
                    InformationText(text = stringResource(id = R.string.input_todo))
                } else {
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        itemsIndexed(
                            items = state.todos,
                            key = { index, item -> item.id }) { index, item ->
                            TodoItem(item) {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TodoItem(item: TodoCloud, click: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(8.dp)
            .clickable(onClick = click)
    ) {
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            text = item.title,
            style = TextStyle(fontWeight = if (item.selected) FontWeight.Bold else FontWeight.Normal)
        )
    }
}

@Composable
fun BottomSheet(initial: List<Int>, apply: (List<Int>) -> Unit) {
    var isCheckedOrdinary by remember { mutableStateOf(1 in initial) }
    var isCheckedFavorite by remember { mutableStateOf(2 in initial) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
        ) {
            Text(
                text = stringResource(R.string.filter_selection),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = stringResource(R.string.ordinary)
                )
                Checkbox(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    checked = isCheckedOrdinary,
                    onCheckedChange = { value ->
                        isCheckedOrdinary = value
                        if (value)
                            isCheckedFavorite = false
                    })
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = stringResource(R.string.favorite)
                )
                Checkbox(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    checked = isCheckedFavorite,
                    onCheckedChange = { value ->
                        isCheckedFavorite = value
                        if (value)
                            isCheckedOrdinary = false
                    })
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            onClick = {
                val filtered = mutableListOf<Int>()
                if (isCheckedOrdinary) filtered.add(1)
                if (isCheckedFavorite) filtered.add(2)
                apply(filtered)
            }) {
            Text(text = stringResource(R.string.apply))
        }
    }
}

@Composable
fun InformationText(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(modifier = Modifier.align(Alignment.Center), text = text)
    }
}