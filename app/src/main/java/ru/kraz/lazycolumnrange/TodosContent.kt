package ru.kraz.lazycolumnrange

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodosContent(
    todos: List<TodoCloud>,
    filterMode: () -> Unit,
    searchMode: () -> Unit,
    clickTodo: (Int) -> Unit,
    longClickTodo: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(navigationIcon = {
                Image(
                    modifier = Modifier.clickable(onClick = filterMode),
                    painter = painterResource(id = R.drawable.filter_list),
                    contentDescription = null
                )
            }, title = {
                Text(text = "TopAppBar")
            }, actions = {
                Image(
                    modifier = Modifier.clickable(onClick = searchMode),
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(
                items = todos,
                key = { index, item -> item.id }) { index, item ->
                TodoItem(item, click = {
                    clickTodo(index)
                }, longClick = {
                    longClickTodo(index)
                } )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoItem(item: TodoCloud, click: () -> Unit, longClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(8.dp)
            .combinedClickable(onClick = click, onLongClick = longClick)
    ) {
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            text = item.title,
            style = TextStyle(fontWeight = if (item.selected) FontWeight.Bold else FontWeight.Normal)
        )
    }
}