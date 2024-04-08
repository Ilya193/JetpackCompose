package ru.kraz.lazycolumnrange

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    val permission =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {}

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        context.locationChecker.collect {}
    }

    var location by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    val locationManager = remember { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    val locationListener = remember {
        LocationListener {
            location = Pair(it.latitude, it.longitude)
        }
    }

    location?.let {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("geo:${it.first}, ${it.second}")
            )
        )
        locationManager.removeUpdates(locationListener)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (location != null) location?.let {
                Text("Долгота: ${it.first}, Широта: ${it.second}", textAlign = TextAlign.Center)
            }
            else Text("Здесь появится широта и долгота")
            Button(onClick = {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        locationListener
                    )
                }
            }) {
                Text("Определить местоположение")
            }
        }
    }
}

val Context.locationChecker: Flow<Pair<Double, Double>>
    get() = callbackFlow {
        if (ContextCompat.checkSelfPermission(
                this@locationChecker,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = this@locationChecker.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val locationListener = LocationListener {
                     trySend(Pair(it.latitude, it.longitude))
                }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )

            awaitClose {
                locationManager.removeUpdates(locationListener)
            }
        }
    }