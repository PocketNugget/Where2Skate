package com.example.where2skate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.where2skate.navigation.AppScreen
import com.example.where2skate.ui.screens.AddSkateparkScreen
import com.example.where2skate.ui.screens.LoginScreen
import com.example.where2skate.ui.screens.MapScreen
import com.example.where2skate.ui.screens.RegisterScreen
import com.example.where2skate.ui.theme.Where2SkateTheme
import com.example.where2skate.ui.viewmodel.AuthViewModel
import com.google.android.gms.maps.model.LatLng
import androidx.compose.runtime.remember

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Where2SkateTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val startDestination = remember(currentUser) {
        if (currentUser != null) AppScreen.MapScreen.route else AppScreen.LoginScreen.route
    }

    // Este LaunchedEffect observará los cambios en startDestination
    // y navegará si es necesario. Es útil si el estado de autenticación cambia
    // mientras la app está abierta.
    LaunchedEffect(startDestination) {
        navController.navigate(startDestination) {
            // Limpia el backstack para que el usuario no vuelva a la pantalla anterior (login/mapa)
            // usando el botón de atrás si el estado de autenticación cambia.
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
            launchSingleTop = true // Evita múltiples copias de la misma pantalla en el backstack
        }
    }


    NavHost(navController = navController, startDestination = AppScreen.SplashScreen.route) { // Inicia con Splash para decidir
        composable(AppScreen.SplashScreen.route) {
            // Aquí puedes tener una pantalla de carga o simplemente decidir la ruta
            // y navegar inmediatamente. Para simplicidad, vamos a asumir que el LaunchedEffect de arriba
            // ya maneja la navegación inicial basada en el estado del currentUser.
            // Si la navegación inicial en LaunchedEffect(startDestination) es suficiente,
            // puedes hacer que SplashScreen navegue directamente.
            LaunchedEffect(Unit) { // Se ejecuta una vez cuando SplashScreen se compone
                val destination = if (authViewModel.currentUser.value != null) AppScreen.MapScreen.route else AppScreen.LoginScreen.route
                navController.navigate(destination) {
                    popUpTo(AppScreen.SplashScreen.route) { inclusive = true }
                }
            }
        }
        composable(AppScreen.LoginScreen.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppScreen.MapScreen.route) {
                        popUpTo(AppScreen.LoginScreen.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(AppScreen.RegisterScreen.route) }
            )
        }
        composable(AppScreen.RegisterScreen.route) {
            RegisterScreen( // Necesitarás crear este Composable similar a LoginScreen
                onRegisterSuccess = {
                    navController.navigate(AppScreen.MapScreen.route) {
                        popUpTo(AppScreen.RegisterScreen.route) { inclusive = true } // O popUpTo LoginScreen
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(AppScreen.MapScreen.route) {
            MapScreen(
                onNavigateToAddSkatepark = { latLng ->
                    // Pasa lat y lng como argumentos a la ruta
                    navController.navigate("${AppScreen.AddSkateparkScreen.route}/${latLng.latitude}/${latLng.longitude}")
                },
                onLogout = {
                    navController.navigate(AppScreen.LoginScreen.route) {
                        popUpTo(AppScreen.MapScreen.route) { inclusive = true }
                    }
                }
            )
        }
        // Definir ruta para AddSkateparkScreen con argumentos para latitud y longitud
        composable(
            route = "${AppScreen.AddSkateparkScreen.route}/{lat}/{lng}",
            arguments = listOf(
                navArgument("lat") { type = NavType.FloatType },
                navArgument("lng") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getFloat("lat")?.toDouble()
            val lng = backStackEntry.arguments?.getFloat("lng")?.toDouble()
            val initialLatLng = if (lat != null && lng != null) LatLng(lat, lng) else null

            AddSkateparkScreen(
                initialLatLng = initialLatLng,
                onSkateparkAdded = { navController.popBackStack() }
            )
        }
    }
}