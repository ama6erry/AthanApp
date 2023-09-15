package com.toot.MinimalAthanApp

sealed class Screen(val route : String){
    object PrayerTimeScreen : Screen("prayer_screen")
    object SettingsScreen : Screen("settings_screen")
}
