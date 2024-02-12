package ru.kraz.lazycolumnrange

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
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
    var showBottomSheet by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            showBottomSheet = true
        }
    }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .width(100.dp)
            .height(50.dp)
    ) {
        Button(onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ) showBottomSheet = true
                else launcher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            else launcher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }) {
            Text(text = "Выбрать картинку")
        }
    }

    if (showBottomSheet)
        BottomSheet {
            showBottomSheet = false
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(dismiss: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val images = remember { mutableStateListOf<Image>() }
    var count by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.ImageColumns.DATA),
            null,
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val data = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                images.add(Image(path = cursor.getString(data)))
            }
        }
    }

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        onDismissRequest = {
            dismiss()
        },
        sheetState = sheetState
    ) {
        Text(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterHorizontally),
            text = stringResource(R.string.selected_images, count)
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier
                    .width(35.dp)
                    .height(35.dp)
                    .align(Alignment.End)
                    .clickable {
                        scope
                            .launch { sheetState.hide() }
                            .invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    dismiss()
                                }
                            }
                    },
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = null
            )
            LazyColumn {
                itemsIndexed(images) { index, item ->
                    Box(modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .padding(if (item.selected) 4.dp else 0.dp)
                        .clickable {
                            scope.launch {
                                val value = images[index]
                                if (value.selected) count--
                                else count++
                                images[index] = value.copy(selected = !value.selected)
                            }
                        }) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxSize(),
                            model = item.path,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .size(size = 20.dp)
                                .padding(top = 2.dp, end = 2.dp)
                                .border(
                                    width = 1.dp,
                                    color = colorResource(id = R.color.blue),
                                    CircleShape
                                )
                                .background(
                                    shape = CircleShape,
                                    color = if (item.selected) colorResource(id = R.color.blue) else Color.Transparent
                                )
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }
        }
    }
}

data class Image(
    val id: Long = System.currentTimeMillis(),
    val path: String,
    val selected: Boolean = false
)

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LazyColumnRangeTheme {
        LazyColumnRange()
    }
}