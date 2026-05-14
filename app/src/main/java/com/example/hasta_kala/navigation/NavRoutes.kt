package com.example.hasta_kala.navigation

sealed class NavRoutes(val route: String) {
    object Setup : NavRoutes("setup")
    object Verify : NavRoutes("verify")
    object SetMpin : NavRoutes("setMpin")
    object Login : NavRoutes("login")
    object Dashboard : NavRoutes("dashboard")
    object Sell : NavRoutes("sell")
    object Bill : NavRoutes("bill/{saleId}") {
        fun createRoute(saleId: Int) = "bill/$saleId"
    }
    object Stock : NavRoutes("stock")
    object StockDetail : NavRoutes("stockDetail/{productId}") {
        fun createRoute(productId: Int) = "stockDetail/$productId"
    }
    object ProductDetails : NavRoutes("productDetails/{productId}") {
        fun createRoute(productId: Int) = "productDetails/$productId"
    }
    object History : NavRoutes("history")
    object Analytics : NavRoutes("analytics")
    object AddProduct : NavRoutes("addProduct")
    object EditProduct : NavRoutes("editProduct/{productId}") {
        fun createRoute(productId: Int) = "editProduct/$productId"
    }
    object SecurityVerify : NavRoutes("security_verify/{otp}") {
        fun createRoute(otp: String) = "security_verify/$otp"
    }
    object ResetMpin : NavRoutes("reset_mpin")
    object Settings : NavRoutes("settings")
}
