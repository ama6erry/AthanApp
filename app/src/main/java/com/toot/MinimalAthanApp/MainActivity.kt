package com.toot.MinimalAthanApp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.toot.MinimalAthanApp.ui.theme.HttpRequestsCurrencyConverterTheme
import com.toot.MinimalAthanApp.ui.theme.systemBarColor
import com.toot.httprequests_currencyconverter.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.GregorianCalendar


class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var navController: NavHostController

    var fetchedLocation : MutableList<Double> = mutableListOf(0.0, 0.0)

    @SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            systemBarColor(color = Color(0xFF212121))

            HttpRequestsCurrencyConverterTheme {
                // A surface container using the 'background' color from the theme


                var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

                navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = backgroundColor
                ) {

                    //var fetchedLocation : MutableList<Double> = mutableListOf(0.0, 0.0)

                    val coroutineScope = rememberCoroutineScope()

                    val context = LocalContext.current

                    val preferencesManager = remember { PreferencesManager(context) }

                    var locationReceived : Boolean by remember { mutableStateOf(false) }

                    val navBarItems = listOf(
                        BottomNavigationItem(
                            title = "Prayer Times",
                            selectedIcon = Icons.Filled.Home,
                            unselectedIcon = Icons.Outlined.Home
                        ),
                        BottomNavigationItem(
                            title = "Settings",
                            selectedIcon = Icons.Filled.Settings,
                            unselectedIcon = Icons.Outlined.Settings
                        )
                    )


                    fun parseLocation(lat : Double, long : Double) {
                        val currentUserLocation : MutableList<Double> = arrayListOf(0.0, 0.0)

                        currentUserLocation.set(0, lat)
                        currentUserLocation.set(1, long)

                        fetchedLocation = currentUserLocation
                    }


                    fun fetchLocation(){
                        val task = fusedLocationClient.lastLocation

                        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)
                        }

                        task.addOnSuccessListener {
                            if(it != null) {
                               val lat = it.latitude
                               val long = it.longitude

                               parseLocation(lat, long)
                            }
                        }
                    }


                    suspend fun updatePrayerTimes(){
                        var count = 0

                        while (true){
                            fetchLocation()
                            count++
                            if (fetchedLocation[0] != 0.0 && fetchedLocation[1] != 0.0){
                                break
                            }
                            delay(15)
                        }

                        preferencesManager.saveData("latitude", fetchedLocation[0].toString())
                        preferencesManager.saveData("longitude", fetchedLocation[1].toString())

                        locationReceived = true
                    }

                    LaunchedEffect(key1 = fetchedLocation, block = { coroutineScope.launch { updatePrayerTimes() }})

                    Scaffold (
                        bottomBar = {
                            NavigationBar(modifier = Modifier.background(boxColor) ,containerColor = boxColor, contentColor = accentColorOne) {
                                navBarItems.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        selected = selectedItemIndex == index,
                                        onClick = { selectedItemIndex = index

                                                    if(selectedItemIndex == 0){
                                                        navController.navigate(route = Screen.PrayerTimeScreen.route)
                                                    }

                                                    if(selectedItemIndex == 1){
                                                        navController.navigate(route = Screen.SettingsScreen.route)
                                                    }

                                                  },
                                        icon = {
                                            Box(modifier = Modifier){
                                                Icon(imageVector =
                                                if(index == selectedItemIndex){
                                                    item.selectedIcon
                                                }else item.unselectedIcon,
                                                    contentDescription = item.title,
                                                    tint = Color.White)
                                            }
                                    },
                                        label = { Text(text = item.title, color = textColor) },
                                        colors = androidx.compose.material3.NavigationBarItemDefaults
                                            .colors(
                                                indicatorColor = Color(4281813333)
                                            )
                                        )}
                            }
                        }
                    ){
                        SetupNavGraph(navController = navController)
                    }

                }
            }
        }
    }


}






