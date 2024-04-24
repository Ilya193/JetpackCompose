package ru.kraz.lazycolumnrange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import ru.kraz.lazycolumnrange.ui.theme.LazyColumnRangeTheme

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

@Composable
fun Content(viewModel: MainViewModel = koinViewModel()) {
    val words by viewModel.words.collectAsStateWithLifecycle(listOf())

    Box(
        modifier = Modifier.fillMaxSize().systemBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(words, key = { word -> word.id }) {
                WordItem(it)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun WordItem(word: WordUi) {
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp),
    ) {
        word.letters.forEach {
            when (it) {
                is LettersMode.Basis -> BasisMode(it)
                is LettersMode.Ending -> EndingMode(it.letters)
            }
        }
    }
}

@Composable
private fun BasisMode(letters: LettersMode.Basis) {
    Row(
        modifier = Modifier.wrapContentSize().drawBehind {
            drawLine(
                color = Color.Black,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height)
            )
        },
    ) {
        RootMode(letters.root)
        letters.suffix?.let {
            it.split(" ").forEach {
                SuffixMode(it)
            }
        }
    }
}

@Composable
private fun RootMode(letters: String) {
    Text(
        modifier = Modifier.wrapContentSize().drawBehind {
            val rect = Rect(
                left = 0f,
                top = size.height - size.width / 2,
                right = size.width,
                bottom = size.height
            )

            drawArc(
                color = Color.Black,
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                topLeft = rect.topLeft,
                size = rect.size,
                style = Stroke(2f)
            )
        },
        text = letters,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun SuffixMode(letters: String) {
    Text(
        modifier = Modifier.wrapContentSize().drawBehind {
            drawLine(
                color = Color.Black,
                start = Offset(0f, 10f),
                end = Offset(size.width / 2, -size.height / 2),
                strokeWidth = 2f
            )

            drawLine(
                color = Color.Black,
                start = Offset(size.width / 2, -size.height / 2),
                end = Offset(size.width, 10f),
                strokeWidth = 2f
            )
        },
        text = letters,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun EndingMode(letters: String) {
    Text(
        modifier = Modifier.wrapContentSize().drawBehind {
            drawRect(
                color = Color.Black,
                topLeft = Offset(0f, 0f),
                style = Stroke(2f)
            )
        },
        text = letters,
        style = MaterialTheme.typography.bodyLarge
    )
}