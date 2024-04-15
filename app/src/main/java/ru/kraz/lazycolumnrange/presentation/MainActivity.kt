package ru.kraz.lazycolumnrange.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ru.kraz.lazycolumnrange.R
import ru.kraz.lazycolumnrange.presentation.ui.theme.LazyColumnRangeTheme
import ru.kraz.lazycolumnrange.presentation.ui.theme.darkOrange

@AndroidEntryPoint
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Content(viewModel: MainViewModel = hiltViewModel()) {
    val interactionSource = remember { MutableInteractionSource() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkOrange),
                title = { Text(text = stringResource(R.string.title_app)) },
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
                    note = note,
                    delete = { viewModel.action(Event.DeleteNote(note)) },
                    completed = { viewModel.action(Event.CompletedNote(note)) })
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
fun NoteItem(modifier: Modifier, note: NoteUi, delete: () -> Unit, completed: () -> Unit) {
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
                .clickable(onClick = completed)
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp),
                text = note.title,
                style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Normal)
            )

            if (note.isCompleted) {
                Image(
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp),
                    imageVector = Icons.Default.Done,
                    contentDescription = null
                )
            }
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
                Text(modifier = Modifier.align(Alignment.TopCenter), text = stringResource(R.string.note_title))

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
                        Text(text = stringResource(R.string.cancel), style = TextStyle(color = Color.Black))
                    }
                    Button(
                        onClick = { add(note) },
                        colors = ButtonDefaults.buttonColors(containerColor = darkOrange)
                    ) {
                        Text(text = stringResource(R.string.add), style = TextStyle(color = Color.Black))
                    }
                }
            }
        }
    }
}