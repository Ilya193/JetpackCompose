package ru.kraz.lazycolumnrange

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
                    Content()
                }
            }
        }
    }
}

@Composable
fun Content(viewModel: MainViewModel = MainViewModel()) {
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
            Text(text = stringResource(R.string.select_image))
        }
    }

    if (showBottomSheet)
        BottomSheet {
            showBottomSheet = false
        }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LazyColumnRangeTheme {
        Content()
    }
}