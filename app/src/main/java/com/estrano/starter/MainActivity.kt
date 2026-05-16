package com.estrano.starter

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.estrano.core.session.SessionManager
import com.estrano.starter.view.compose.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Exit app instead of going back to Auth
                finishAffinity()
            }
        })
        
        setContent {
            EstranoTheme {
                val navController = rememberNavController()
                val items = listOf(Screen.Home, Screen.Services, Screen.Portfolio, Screen.Profile)

                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = Color(0xFF1E1E1E)) {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = null) },
                                    label = { Text(screen.label) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF03DAC5),
                                        indicatorColor = Color.Transparent
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(navController, startDestination = Screen.Home.route, modifier = Modifier.padding(innerPadding)) {
                        composable(Screen.Home.route) { 
                            HomeScreen(featuredProducts = emptyList(), marketProducts = emptyList(), onProductClick = {})
                        }
                        composable(Screen.Services.route) { 
                            ServicesScreen(allProducts = emptyList(), onProductClick = {})
                        }
                        composable(Screen.Portfolio.route) { 
                            PortfolioScreen(orderCount = 0, totalSpent = 0.0)
                        }
                        composable(Screen.Profile.route) { 
                            ProfileScreen(
                                userName = sessionManager.getName(),
                                userEmail = sessionManager.getEmail(),
                                onLogout = { 
                                    sessionManager.signOut()
                                    finish()
                                },
                                onDiscordClick = { },
                                onStealthToggle = { }
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Market", Icons.Default.Home)
    object Services : Screen("services", "Products", Icons.Default.ShoppingCart)
    object Portfolio : Screen("portfolio", "Orders", Icons.Default.List)
    object Profile : Screen("profile", "Account", Icons.Default.AccountCircle)
}
