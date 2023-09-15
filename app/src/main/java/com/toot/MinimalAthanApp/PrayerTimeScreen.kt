package com.toot.MinimalAthanApp

import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azan.Azan
import com.azan.Method
import com.azan.astrologicalCalc.Location
import com.azan.astrologicalCalc.SimpleDate
import com.toot.MinimalAthanApp.ui.theme.systemBarColor
import com.toot.httprequests_currencyconverter.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

var backgroundColor = Color(4280361249)
var boxColor = Color(4281282351)
var secondaryBoxColor = Color(	4282664004)
var accentColorOne = Color(4278218147)
var textColor = Color(4294967295)
var accentTextColor = Color(	4288010691)



@Composable
fun PrayerTimeScreen(){


    val context = LocalContext.current

    val preferencesManager = remember { PreferencesManager(context) }

    var locationReceived : Boolean by remember { mutableStateOf(false)}

    var latitude : Double
    var longitude : Double

    val coroutineScope = rememberCoroutineScope()

    var timeUntil : String by remember { mutableStateOf("Loading..") }

    var fajrTime : String by remember { mutableStateOf("") }
    var shurooqTime : String by remember { mutableStateOf("") }
    var dhurTime : String by remember { mutableStateOf("") }
    var asrTime : String by remember { mutableStateOf("") }
    var maghribTime : String by remember { mutableStateOf("") }
    var ishaTime : String by remember { mutableStateOf("") }



    val today = SimpleDate(GregorianCalendar())





    if(!locationReceived){
        android.os.Handler().postDelayed({
            latitude = preferencesManager.getData("latitude", "").toDouble()
            longitude = preferencesManager.getData("longitude", "").toDouble()
            //Toast.makeText(context, "$latitude,, $longitude", Toast.LENGTH_SHORT).show()
            val location = Location(latitude, longitude, gmtOffset(), observeDST())
            val azan = Azan(location, Method.UMM_ALQURRA)
            val prayerTimes = azan.getPrayerTimes(today)
            fajrTime = prayerTimes.fajr().toString()
            shurooqTime = prayerTimes.shuruq().toString()
            dhurTime = prayerTimes.thuhr().toString()
            asrTime = prayerTimes.assr().toString()
            maghribTime = prayerTimes.maghrib().toString()
            ishaTime = prayerTimes.ishaa().toString()

            preferencesManager.saveData("fajrTime", fajrTime)
            preferencesManager.saveData("shurooqTime", shurooqTime)
            preferencesManager.saveData("dhurTime", dhurTime)
            preferencesManager.saveData("asrTime", asrTime)
            preferencesManager.saveData("maghribTime", maghribTime)
            preferencesManager.saveData("ishaTime", ishaTime)

            locationReceived = true
        }, 20)
    }

    fun currentPrayer(): String{
        val listOfPrayers = listOf<String>(fajrTime, shurooqTime, dhurTime, asrTime, maghribTime, ishaTime)
        val listOfTimes = mutableListOf<Date>(Date(), Date(), Date(), Date(), Date(), Date())
        val formatter = SimpleDateFormat("HH:mm:ss")
        var count = 0
        val date = Date()
        val current = formatter.format(date)

        val currentTime = formatter.parse(current)

        for(i in listOfPrayers){
            listOfTimes[count] = formatter.parse(i)!!
            count += 1
        }

        println(listOfTimes)
        println(listOfTimes[1])
        println(currentTime)

        if (currentTime != null) {
            return if (currentTime.before(listOfTimes[1]) && currentTime.after(listOfTimes[0])){
                "Fajr"
            } else if (currentTime.before(listOfTimes[2]) && currentTime.after(listOfTimes[1])){
                "Shurooq"
            } else if (currentTime.before(listOfTimes[3]) && currentTime.after(listOfTimes[2])){
                "Dhur"
            } else if (currentTime.before(listOfTimes[4]) && currentTime.after(listOfTimes[3])){
                "Asr"
            } else if (currentTime.before(listOfTimes[5]) && currentTime.after(listOfTimes[4])){
                "Maghrib"
            } else {
                "Isha"
            }
        }

        return "Error"
    }

    fun nextPrayer(): String{
        when(currentPrayer()){
            "Fajr" -> return "Shurooq"
            "Shurooq" -> return "Dhur"
            "Dhur" -> return "Asr"
            "Asr" -> return "Maghrib"
            "Maghrib" -> return "Isha"
            "Isha" -> return "Fajr"
        }

        return "Error"
    }

    fun timeToNextPrayer(): String{
        val listOfPrayers = listOf<String>(fajrTime, shurooqTime, dhurTime, asrTime, maghribTime, ishaTime)
        val listOfTimes = mutableListOf<Date>(Date(), Date(), Date(), Date(), Date(), Date())
        val formatter = SimpleDateFormat("HH:mm:ss")
        var count = 0
        val date = Date()
        val current = formatter.format(date)
        val targetTime : Long

        val currentTime = formatter.parse(current).time

        for(i in listOfPrayers){
            listOfTimes[count] = formatter.parse(i)!!
            count += 1
        }

        if(currentPrayer() == "Fajr"){
            targetTime = listOfTimes[1].time
        } else if (currentPrayer() == "Shurooq"){
            targetTime = listOfTimes[2].time
        } else if (currentPrayer() == "Dhur"){
            targetTime = listOfTimes[3].time
        } else if (currentPrayer() == "Asr"){
            targetTime = listOfTimes[4].time
        } else if (currentPrayer() == "Maghrib"){
            targetTime = listOfTimes[5].time
        } else {
            targetTime = listOfTimes[0].time + 86400000
        }

        var difference = (targetTime - currentTime) / 1000

        val hours = difference / 3600
        difference %= 3600
        val minutes = difference / 60
        difference %= 60
        val seconds = difference

        return TimeInHours(hours.toInt(), minutes.toInt(), seconds.toInt()).toString()
    }

    if(locationReceived){
        timeUntil = timeToNextPrayer()
    }

    @Composable
    fun CustomText(text : String, color: Color, textSize : TextUnit, prayerTime: String, flag : Boolean = false){
        Row(modifier = Modifier
            .height(30.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(240f, fill = true)){
                Text(text = text, color = if(locationReceived && currentPrayer() == text){color}else{Color.White}, fontSize = textSize)
            }
            Box(modifier = Modifier.weight(400f,fill = true), contentAlignment = Alignment.Center){
                Image(painter = painterResource(id = if(flag){R.drawable.soundiconoutlined}else{R.drawable.soundiconfilled}), contentDescription = "Toggle notifications", colorFilter = ColorFilter.tint(accentColorOne))
            }
            Box(modifier = Modifier.weight(240f,fill = true), contentAlignment = Alignment.CenterEnd){
                Text(text = prayerTime, color = if(locationReceived && currentPrayer() == text){color}else{Color.White}, fontSize = textSize)
            }
        }
    }

    systemBarColor(color = Color(0xFF212121))

    Surface(modifier = Modifier.fillMaxSize(),
        color = backgroundColor) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

            Column {
                Box(modifier = Modifier
                    .weight(400f)
                    .fillMaxSize()
                    .padding(
                        start = PixelToDp(pixelSize = 30),
                        top = PixelToDp(pixelSize = 30),
                        end = PixelToDp(pixelSize = 30),
                        bottom = PixelToDp(pixelSize = 15)
                    )
                    .clip(shape = RoundedCornerShape(15.dp))
                    .background(boxColor)){
                    Row {
                        Box(modifier = Modifier
                            .padding(PixelToDp(pixelSize = 28), PixelToDp(pixelSize = 28), PixelToDp(pixelSize = 14), PixelToDp(pixelSize = 28))
                            .fillMaxHeight()
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(secondaryBoxColor)
                            .weight(1f, fill = true),
                            contentAlignment = Alignment.CenterStart){
                            Column(modifier = Modifier.padding(PixelToDp(pixelSize = 50)), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start) {
                                Text(text = "It's currently", color = Color.White, fontSize = 15.sp, modifier = Modifier.offset(0.dp, 5.dp))
                                Text(text = if(locationReceived) currentPrayer() else "Loading..", fontSize = 35.sp, color = accentTextColor, modifier = Modifier.offset((-1.75).dp, (-5).dp))
                            }

                        }
                        Box(modifier = Modifier
                            .padding(PixelToDp(pixelSize = 14), PixelToDp(pixelSize = 28), PixelToDp(pixelSize = 28), PixelToDp(pixelSize = 28))
                            .fillMaxHeight()
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(secondaryBoxColor)
                            .weight(1f, fill = true),
                            contentAlignment = Alignment.CenterStart){
                            Column(modifier = Modifier.padding(PixelToDp(pixelSize = 50)), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.Start){
                                Text(text = if (locationReceived)"${nextPrayer()} is in" else "Loading..", color = Color.White, fontSize = 15.sp, modifier = Modifier.offset(0.dp, 5.dp))
                                Text(text = if(locationReceived){timeUntil}else{"Loading.."}, color = accentTextColor, fontSize = 35.sp, modifier = Modifier.offset((-1.75).dp, (-5).dp))
                            }


                        }
                        

                    }
                }

                LaunchedEffect(key1 = timeUntil, block = {coroutineScope.launch {
                    if (locationReceived){
                        Timer().scheduleAtFixedRate(1, 1000){
                            timeUntil = timeToNextPrayer()
                        }
                    }
                } } )



                Box(modifier = Modifier
                    .weight(950f)
                    .fillMaxSize()
                    .padding(
                        start = PixelToDp(pixelSize = 30),
                        top = PixelToDp(pixelSize = 30),
                        end = PixelToDp(pixelSize = 30),
                        bottom = PixelToDp(pixelSize = 15)
                    )
                    .clip(shape = RoundedCornerShape(15.dp))
                    .background(boxColor),
                    contentAlignment = Alignment.Center){

                    Column(modifier = Modifier.padding(PixelToDp(pixelSize = 75)), verticalArrangement = Arrangement.Center) {
                        CustomText(text = "Fajr", color = accentTextColor, textSize = 20.sp, prayerTime = fajrTime.dropLast(3))
                        CustomText(text = "Shurooq", color = accentTextColor, textSize = 20.sp, prayerTime = shurooqTime.dropLast(3))
                        CustomText(text = "Dhur", color = accentTextColor, textSize = 20.sp, prayerTime = dhurTime.dropLast(3))
                        CustomText(text = "Asr", color = accentTextColor, textSize = 20.sp, prayerTime = asrTime.dropLast(3))
                        CustomText(text = "Maghrib", color = accentTextColor, textSize = 20.sp, prayerTime = maghribTime.dropLast(3))
                        CustomText(text = "Isha", color = accentTextColor, textSize = 20.sp, prayerTime = ishaTime.dropLast(3))
                    }

                }

                Box(modifier = Modifier
                    .weight(170f)
                    .fillMaxSize()
                    .padding(
                        start = PixelToDp(pixelSize = 30),
                        top = PixelToDp(pixelSize = 30),
                        end = PixelToDp(pixelSize = 30),
                        bottom = PixelToDp(pixelSize = 15)
                    )
                    .clip(shape = RoundedCornerShape(15.dp))
                    .background(boxColor)){
                    
                    //Text(text = if(locationReceived){getAddress(latitude, longitude, context = context)}else{"Getting address.."})
                    
                }

                Spacer(modifier = Modifier.weight(612f))

            }

        }
    }
}

fun gmtOffset(): Double{
    val timeZone = TimeZone.getDefault()
    print(timeZone.rawOffset / 3600000)
    print(timeZone.rawOffset)
    print(timeZone)

    return (timeZone.rawOffset / 3600000).toDouble()
}

fun observeDST(): Int{
    val timeZone = TimeZone.getDefault().inDaylightTime(Date())

    if (timeZone){
        return 1
    } else {
        return 0
}
}

@Composable
fun PixelToDp(pixelSize: Int): Dp {
    return with(LocalDensity.current) { pixelSize.toDp() }
}

fun getAddress(lat : Double, long : Double, context: Context): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    val geocodeListener = Geocoder.GeocodeListener { addresses ->
        val city = addresses[0].adminArea
        val country = addresses[0].countryName
    }

    if(Build.VERSION.SDK_INT >= 33){
        val list = geocoder.getFromLocation(lat, long, 1, geocodeListener)

    } else {
        val list = geocoder.getFromLocation(lat, long, 1)
    }

    return "Error"

}

@Preview
@Composable
fun ComposablePreview(){
    Box(modifier = Modifier
        .padding(PixelToDp(pixelSize = 28))
        .height(100.dp)
        .clip(shape = RoundedCornerShape(10.dp))
        .background(secondaryBoxColor),
        contentAlignment = Alignment.CenterStart){
        Column(modifier = Modifier.padding(PixelToDp(pixelSize = 15)), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "It's currently", color = Color.White, modifier = Modifier.offset(0.dp, 10.dp), fontSize = 20.sp)
            Text(text = "Isha", fontSize = 40.sp, color = accentTextColor, lineHeight = 10.sp)
        }

    }
}
