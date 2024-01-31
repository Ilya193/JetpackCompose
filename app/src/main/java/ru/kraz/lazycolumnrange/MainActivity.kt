package ru.kraz.lazycolumnrange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
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
                    LazyColumnRange()
                }
            }
        }
    }
}

@Composable
fun LazyColumnRange(viewModel: MainViewModel = MainViewModel()) {
    val messages by viewModel.uiState.collectAsState()

    var textFieldValue by rememberSaveable { mutableStateOf("") }

    Column {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn {
                items(messages) {
                    Message(item = it)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                TextField(modifier = Modifier.weight(1f),
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                    })
                Image(
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .clickable {

                            if (textFieldValue.isNotEmpty()) {
                                viewModel.sendMessage(textFieldValue)
                                textFieldValue = ""
                            }
                        },
                    painter = painterResource(id = android.R.drawable.ic_menu_send),
                    contentDescription = null
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Message(item: MessageUi) {
    if (item.iSendThis) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(4.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Card(modifier = Modifier
                .fillMaxWidth(0.6f)
                .fillMaxHeight()
                .align(Alignment.TopEnd), onClick = {}) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(modifier = Modifier.wrapContentSize(), text = item.text)
                    Image(
                        modifier = Modifier
                            .width(15.dp)
                            .height(15.dp)
                            .align(Alignment.End),
                        painter = painterResource(id = if (item.messageRead) R.drawable.done_all else R.drawable.done),
                        contentDescription = null
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(4.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Card(modifier = Modifier
                .fillMaxWidth(0.6f)
                .fillMaxHeight(), onClick = {}) {
                Text(modifier = Modifier.padding(4.dp), text = item.text)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LazyColumnRangeTheme {
        LazyColumnRange()
    }
}