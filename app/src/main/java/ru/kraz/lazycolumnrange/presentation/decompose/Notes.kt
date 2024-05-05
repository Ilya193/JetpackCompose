package ru.kraz.lazycolumnrange.presentation.decompose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.kraz.lazycolumnrange.R
import ru.kraz.lazycolumnrange.presentation.ui.theme.darkOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notes(component: NotesComponent) {
    val state by component.state.collectAsStateWithLifecycle()
    val interactionSource = remember { MutableInteractionSource() }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkOrange),
                title = { Text(text = stringResource(R.string.title_app)) },
                actions = {
                    Image(
                        modifier = Modifier.clickable(interactionSource, null) {
                            component.showDialog(true)
                        },
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(state.items, key = { item -> item.id }) { note ->
                NoteItem(
                    modifier = Modifier,
                    note = note,
                    delete = { component.onNoteDeleted(note) },
                    completed = { component.onNoteClicked(note) })
                HorizontalDivider()
            }
        }

        if (state.showDialog) {
            DialogAddNote(
                add = {
                    if (it.isNotEmpty()) component.addNote(it)
                },
                onDismiss = { component.showDialog(false) })
        }
    }
}