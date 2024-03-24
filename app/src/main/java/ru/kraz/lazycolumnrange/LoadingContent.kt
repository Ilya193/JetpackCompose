package ru.kraz.lazycolumnrange

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoadingContent() {
    Box(modifier = Modifier.wrapContentSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}