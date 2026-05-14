package com.example.hasta_kala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.hasta_kala.data.datastore.ShopPreferences
import com.example.hasta_kala.navigation.AppNavGraph
import com.example.hasta_kala.navigation.NavRoutes
import com.example.hasta_kala.ui.theme.HastaKalaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val shopPreferences = ShopPreferences(this)
        val database = com.example.hasta_kala.data.db.AppDatabase.getInstance(this)
        
        enableEdgeToEdge()
        
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            runOnUiThread {
                android.widget.Toast.makeText(this, "Crash: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
            e.printStackTrace()
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }
        
        setContent {
            val hasMpinSet by shopPreferences.hasMpinSet.collectAsState(initial = null)
            val navController = rememberNavController()
            
            HastaKalaTheme {
                if (hasMpinSet != null) {
                    val startDestination = if (hasMpinSet == true) NavRoutes.Login.route else NavRoutes.Setup.route
                    AppNavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        shopPreferences = shopPreferences,
                        database = database
                    )
                }
            }
        }
    }
}