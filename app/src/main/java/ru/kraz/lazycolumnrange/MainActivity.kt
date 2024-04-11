package ru.kraz.lazycolumnrange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import ru.kraz.lazycolumnrange.ui.theme.LazyColumnRangeTheme
import ru.kraz.lazycolumnrange.ui.theme.darkOrange

@AndroidEntryPoint
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Content(viewModel: MainViewModel = hiltViewModel()) {
    val interactionSource = remember { MutableInteractionSource() }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkOrange),
                title = { Text(text = "Заметки") },
                actions = {
                    Image(
                        modifier = Modifier.clickable(interactionSource, null) {
                            viewModel.action(Event.ShowDialog(true))
                        },
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(uiState.notes, key = { item -> item.id }) { note ->
                NoteItem(
                    modifier = Modifier.animateItemPlacement(animationSpec = tween(250)),
                    note = note
                ) {
                    viewModel.action(Event.DeleteNote(note))
                }
                HorizontalDivider()
            }
        }

        if (uiState.showDialog) {
            DialogAddNote(
                add = {
                    if (it.isNotEmpty()) viewModel.action(Event.AddNote(it))
                },
                onDismiss = { viewModel.action(Event.ShowDialog(false)) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(modifier: Modifier, note: NoteUi, delete: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
        if (it == SwipeToDismissBoxValue.EndToStart) delete()
        true
    })

    SwipeToDismissBox(
        enableDismissFromStartToEnd = false,
        state = dismissState,
        backgroundContent = {
            if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.Red)
                    )
                }
            }
        }) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 16.dp)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = note.title,
                style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Normal)
            )
        }
    }
}

@Composable
fun DialogAddNote(add: (String) -> Unit, onDismiss: () -> Unit) {
    var note by remember {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(modifier = Modifier.align(Alignment.TopCenter), text = "Название заметки")

                OutlinedTextField(
                    modifier = Modifier.align(Alignment.Center),
                    value = note,
                    onValueChange = {
                        note = it
                    },
                    placeholder = {
                        Text(text = "dalvik")
                    })

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = darkOrange)
                    ) {
                        Text(text = "Отменить", style = TextStyle(color = Color.Black))
                    }
                    Button(
                        onClick = { add(note) },
                        colors = ButtonDefaults.buttonColors(containerColor = darkOrange)
                    ) {
                        Text(text = "Добавить", style = TextStyle(color = Color.Black))
                    }
                }
            }
        }
    }
}