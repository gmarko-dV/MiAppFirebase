package com.tiendaapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthApp() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("productList") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("productList") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }

        composable("productList") {
            ProductListScreen(
                onAddProduct = { navController.navigate("addProduct") },
                onEditProduct = { id -> navController.navigate("editProduct/$id") },
                onLogout = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("productList") { inclusive = true }
                    }
                }
            )
        }

        composable("addProduct") {
            AddProductScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = "editProduct/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            EditProductScreen(productId = productId, onBack = { navController.popBackStack() })
        }
    }
}
