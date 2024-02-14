package ru.kraz.lazycolumnrange

import android.provider.MediaStore
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(dismiss: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val images = remember { mutableStateListOf<ImageUi>() }
    var count by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.ImageColumns.DATA),
                null,
                null,
                null
            )?.use { cursor ->
                var id = 0
                val temp = mutableListOf<ImageUi>()
                while (cursor.moveToNext()) {
                    val data = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    temp.add(ImageUi(id = id++, path = cursor.getString(data)))
                }
                images.addAll(temp.reversed())
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
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.Center),
                    text = stringResource(R.string.selected_images, count)
                )
                Image(
                    modifier = Modifier
                        .width(35.dp)
                        .height(35.dp)
                        .align(Alignment.CenterEnd)
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
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3)
            ) {
                itemsIndexed(images, key = { _, item ->
                    item.id
                }) { index, item ->
                    val bgPadding: Dp by animateDpAsState(
                        targetValue = if (item.selected) 8.dp else 2.dp, label = ""
                    )
                    Box(modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .padding(bgPadding)
                        .clickable {
                            scope.launch {
                                val value = images[index]
                                if (value.selected) count--
                                else count++
                                images[index] = value.copy(selected = !value.selected)
                            }
                        }) {
                        val bgColor: Color by animateColorAsState(
                            if (item.selected) colorResource(R.color.blue) else Color.Transparent,
                            animationSpec = tween(150, easing = LinearEasing), label = ""
                        )
                        val alphaValue: Float by animateFloatAsState(
                            targetValue = if (item.selected) 1f else 0f,
                            animationSpec = tween(150, easing = LinearEasing), label = ""
                        )
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
                                    color = bgColor
                                )
                                .align(Alignment.TopEnd)
                        ) {
                            Image(
                                modifier = Modifier.alpha(alphaValue),
                                painter = painterResource(id = R.drawable.ic_done),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }
}