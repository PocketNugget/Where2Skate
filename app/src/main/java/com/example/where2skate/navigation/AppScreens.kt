package com.example.where2skate.navigation

sealed class AppScreen(val route: String) {
    object SplashScreen : AppScreen("splash_screen")
    object LoginScreen : AppScreen("login_screen")
    object RegisterScreen : AppScreen("register_screen")
    object MainScreen : AppScreen("main_screen") // Podría ser un BottomNavHost
    object MapScreen : AppScreen("map_screen")
    object AddSkateparkScreen : AppScreen("add_skatepark_screen")
    // object SkateparkDetailScreen : AppScreen("skatepark_detail_screen/{skateparkId}") {
    //    fun createRoute(skateparkId: String) = "skatepark_detail_screen/$skateparkId"
    // }
    // object ProfileScreen : AppScreen("profile_screen")
}