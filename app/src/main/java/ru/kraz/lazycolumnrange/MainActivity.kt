package ru.kraz.lazycolumnrange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
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

    if (uiState.isLoading) LoadingContent()
    else if (uiState.isError) ErrorContent { }
    else {
        if (uiState.mode is Mode.Search) {
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
                if (uiState.nothingFound is NothingFound.Search) {
                    InformationText(text = stringResource(R.string.nothing_found))
                } else if (uiState.todos.isEmpty()) {
                    InformationText(text = stringResource(id = R.string.input_todo))
                } else {
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        itemsIndexed(
                            items = uiState.todos,
                            key = { index, item -> item.id }) { index, item ->
                            TodoItem(item, click = {}, longClick = {})
                        }
                    }
                }
            }
        } else if (uiState.mode is Mode.Filter) {
            Scaffold(
                topBar = {
                    TopAppBar(navigationIcon = {
                        Box(
                            modifier = Modifier.width(40.dp)
                        ) {
                            if (uiState.filtered.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(17.dp)
                                        .offset(y = (-10).dp)
                                        .clip(CircleShape)
                                        .align(Alignment.TopEnd).background(Color.Red),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = uiState.filtered.size.toString(),
                                        style = TextStyle(
                                            color = Color.White,
                                            fontSize = 11.sp
                                        ),
                                    )
                                }
                            }

                            Image(
                                modifier = Modifier.clickable {
                                    viewModel.action(
                                        Action.FilterMode(
                                            true,
                                            uiState.todos,
                                            uiState.filtered
                                        )
                                    )
                                },
                                painter = painterResource(id = R.drawable.filter_list),
                                contentDescription = null
                            )
                        }

                    }, title = {
                        Text(text = "TopAppBar")
                    }, actions = {
                        Image(modifier = Modifier.clickable {
                            viewModel.action(Action.SearchMode)
                        }, imageVector = Icons.Filled.Search, contentDescription = null)
                    })
                }
            ) { padding ->
                if (uiState.nothingFound is NothingFound.Filter) {
                    InformationText(text = stringResource(R.string.nothing_found))
                } else {
                    LazyColumn(modifier = Modifier.padding(padding)) {
                        itemsIndexed(
                            items = uiState.todos,
                            key = { index, item -> item.id }) { index, item ->
                            TodoItem(item, click = {}, longClick = {})
                        }
                    }
                }

                if ((uiState.mode as Mode.Filter).show)
                    ModalBottomSheet(onDismissRequest = {
                        viewModel.action(
                            Action.FilterMode(
                                false,
                                filtered = uiState.filtered,
                                todos = uiState.todos
                            )
                        )
                    }) {
                        BottomSheet(uiState.filtered) {
                            viewModel.action(Action.ApplyFilter(it))
                        }
                    }
            }
        } else {
            if (uiState.todos.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    TodosContent(
                        uiState.todos,
                        filterMode = {
                            viewModel.action(
                                Action.FilterMode(
                                    mode = true,
                                    filtered = uiState.filtered
                                )
                            )
                        },
                        searchMode = { viewModel.action(Action.SearchMode) },
                        clickTodo = { viewModel.action(Action.ClickTodo(it)) },
                        longClickTodo = { viewModel.action(Action.TodosWithDetailsTodo(it)) })

                    uiState.detailsTodo?.let {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.White)
                                .align(Alignment.BottomCenter)
                        ) {
                            Text(modifier = Modifier.align(Alignment.Center), text = it.title)
                            Image(
                                modifier = Modifier.align(Alignment.TopEnd).clickable {
                                    viewModel.action(Action.CancelDetails)
                                },
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomSheet(initial: List<Int>, apply: (List<Int>) -> Unit) {
    var isCheckedOrdinary by remember { mutableStateOf(1 in initial) }
    var isCheckedFavorite by remember { mutableStateOf(2 in initial) }
    var isCheckedFavoriteFirst by remember { mutableStateOf(3 in initial) }

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
                        if (value) {
                            isCheckedFavorite = false
                            isCheckedFavoriteFirst = false
                        }
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
                        if (value) {
                            isCheckedOrdinary = false
                            isCheckedFavoriteFirst = false
                        }
                    })
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.CenterStart),
                    text = stringResource(R.string.favorite_first)
                )
                Checkbox(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    checked = isCheckedFavoriteFirst,
                    onCheckedChange = { value ->
                        isCheckedFavoriteFirst = value
                        if (value) {
                            isCheckedOrdinary = false
                            isCheckedFavorite = false
                        }
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
                if (isCheckedFavoriteFirst) filtered.add(3)
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