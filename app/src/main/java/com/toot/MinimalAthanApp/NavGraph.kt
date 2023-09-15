package com.toot.MinimalAthanApp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun SetupNavGraph(navController: NavHostController){
    NavHost(navController = navController, startDestination = Screen.PrayerTimeScreen.route){
        composable(route = Screen.PrayerTimeScreen.route){
            PrayerTimeScreen()
        }
        composable(route = Screen.SettingsScreen.route){
            SettingsScreen()
        }
    }
}