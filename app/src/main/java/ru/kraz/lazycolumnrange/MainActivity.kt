package ru.kraz.lazycolumnrange

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyColumnRange(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    viewModel: MainViewModel = MainViewModel()
) {
    val list by viewModel.uiState.collectAsState(initial = emptyList())

    var mediaPlayer: MediaPlayer? = null

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val imagesFromGallery = remember { mutableStateListOf<String>() }

    val soundFromGallery = remember { mutableStateOf<String>("") }

    val pagerState = rememberPagerState { imagesFromGallery.size }

    val launcherImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            it?.let {
                imagesFromGallery.add(it.toString())
            }
        }
    )

    val context = LocalContext.current

    val launcherSound = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            it?.let {
                soundFromGallery.value = it.toString()
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(context, Uri.parse(soundFromGallery.value))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                mediaPlayer?.setOnCompletionListener {
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
            }
        })

    Column {
        Row {
            Button(onClick = { launcherImage.launch("image/*") }) {
                Text(text = "Image from gallery")
            }

            Button(onClick = {
                mediaPlayer?.release()
                mediaPlayer = null
                launcherSound.launch("audio/*")
            }) {
                Text(text = "Audio from memory")
            }
        }

        if (imagesFromGallery.size == 0)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight(Alignment.CenterVertically),
                    text = "Здесь будут изображения из галереи",
                    style = TextStyle(
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    ),
                )
            }

        HorizontalPager(state = pagerState) { page ->
            Box {
                AsyncImage(
                    model = imagesFromGallery[page], contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Crop
                )
                Image(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable {
                            imagesFromGallery.removeAt(page)
                        },
                    painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun Element(item: String) {
    AsyncImage(
        model = item, contentDescription = null,
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LazyColumnRangeTheme {
        LazyColumnRange()
    }
}