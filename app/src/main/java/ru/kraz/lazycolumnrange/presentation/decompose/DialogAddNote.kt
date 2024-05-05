package ru.kraz.lazycolumnrange.presentation.decompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.kraz.lazycolumnrange.R
import ru.kraz.lazycolumnrange.presentation.ui.theme.darkOrange

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
                Text(
                    modifier = Modifier.align(Alignment.TopCenter),
                    text = stringResource(R.string.note_title)
                )

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
                        Text(
                            text = stringResource(R.string.cancel),
                            style = TextStyle(color = Color.Black)
                        )
                    }
                    Button(
                        onClick = { add(note) },
                        colors = ButtonDefaults.buttonColors(containerColor = darkOrange)
                    ) {
                        Text(
                            text = stringResource(R.string.add),
                            style = TextStyle(color = Color.Black)
                        )
                    }
                }
            }
        }
    }
}