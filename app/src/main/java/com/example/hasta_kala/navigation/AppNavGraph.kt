package com.example.hasta_kala.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hasta_kala.data.datastore.ShopPreferences
import com.example.hasta_kala.ui.screens.auth.*
import com.example.hasta_kala.ui.screens.setup.AppSetupScreen
import com.example.hasta_kala.ui.screens.setup.AppSetupViewModel
import com.example.hasta_kala.ui.screens.dashboard.DashboardScreen
import com.example.hasta_kala.ui.screens.dashboard.DashboardViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hasta_kala.data.repository.ProductRepository
import com.example.hasta_kala.data.repository.SaleRepository
import com.example.hasta_kala.ui.screens.sell.SellScreen
import com.example.hasta_kala.ui.screens.sell.SellViewModel
import com.example.hasta_kala.ui.screens.bill.BillScreen
import com.example.hasta_kala.ui.screens.bill.BillViewModel
import com.example.hasta_kala.ui.screens.stock.StockScreen
import com.example.hasta_kala.ui.screens.stock.StockViewModel
import com.example.hasta_kala.ui.screens.stock.add.AddProductScreen
import com.example.hasta_kala.ui.screens.stock.add.AddProductViewModel
import com.example.hasta_kala.ui.screens.history.HistoryScreen
import com.example.hasta_kala.ui.screens.history.HistoryViewModel
import com.example.hasta_kala.ui.screens.analytics.AnalyticsScreen
import com.example.hasta_kala.ui.screens.analytics.AnalyticsViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.first

class ViewModelFactory(
    private val shopPreferences: ShopPreferences,
    private val productRepository: ProductRepository? = null,
    private val saleRepository: SaleRepository? = null,
    private val saleId: Int? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AppSetupViewModel::class.java) -> AppSetupViewModel(shopPreferences) as T
            modelClass.isAssignableFrom(SetMpinViewModel::class.java) -> SetMpinViewModel(shopPreferences) as T
            modelClass.isAssignableFrom(MpinLoginViewModel::class.java) -> MpinLoginViewModel(shopPreferences) as T
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> DashboardViewModel(saleRepository!!, productRepository!!, shopPreferences) as T
            modelClass.isAssignableFrom(SellViewModel::class.java) -> SellViewModel(productRepository!!, saleRepository!!) as T
            modelClass.isAssignableFrom(StockViewModel::class.java) -> StockViewModel(productRepository!!) as T
            modelClass.isAssignableFrom(AddProductViewModel::class.java) -> AddProductViewModel(productRepository!!) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(saleRepository!!) as T
            modelClass.isAssignableFrom(AnalyticsViewModel::class.java) -> AnalyticsViewModel(saleRepository!!) as T
            modelClass.isAssignableFrom(BillViewModel::class.java) -> BillViewModel(saleId!!, saleRepository!!) as T
            modelClass.isAssignableFrom(com.example.hasta_kala.ui.screens.details.ProductDetailsViewModel::class.java) -> {
                com.example.hasta_kala.ui.screens.details.ProductDetailsViewModel(saleId!!, productRepository!!) as T
            }
            modelClass.isAssignableFrom(com.example.hasta_kala.ui.screens.profile.ProfileViewModel::class.java) -> {
                com.example.hasta_kala.ui.screens.profile.ProfileViewModel(shopPreferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String,
    shopPreferences: ShopPreferences,
    database: com.example.hasta_kala.data.db.AppDatabase
) {
    val productRepository = remember { com.example.hasta_kala.data.repository.ProductRepository(database.productDao()) }
    val saleRepository = remember { com.example.hasta_kala.data.repository.SaleRepository(database) }
    
    val factory = remember(shopPreferences, productRepository, saleRepository) {
        ViewModelFactory(shopPreferences, productRepository, saleRepository)
    }

    // Auto-seed database if empty
    LaunchedEffect(Unit) {
        if (productRepository.getProductCount() == 0) {
            productRepository.preSeedProducts()
        }
    }

    var sessionOtp by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.Setup.route) {
            val viewModel: AppSetupViewModel = viewModel(factory = factory)
            val context = androidx.compose.ui.platform.LocalContext.current
            AppSetupScreen(onContinue = { shopName, ownerName, mobile ->
                val randomOtp = (100000..999999).random().toString()
                sessionOtp = randomOtp
                com.example.hasta_kala.util.NotificationHelper.showOtpNotification(context, randomOtp)
                viewModel.saveShopDetails(shopName, ownerName, mobile)
                navController.navigate(NavRoutes.Verify.route)
            })
        }
        
        composable(NavRoutes.Verify.route) {
            OtpScreen(expectedOtp = sessionOtp, onVerify = { navController.navigate(NavRoutes.SetMpin.route) }, onBack = { navController.popBackStack() }, onResend = { })
        }
        
        composable(NavRoutes.SetMpin.route) {
            val viewModel: SetMpinViewModel = viewModel(factory = factory)
            SetMpinScreen(onMpinSet = { mpin ->
                viewModel.saveMpin(mpin)
                navController.navigate(NavRoutes.Dashboard.route) { popUpTo(NavRoutes.Setup.route) { inclusive = true } }
            }, onBack = { navController.popBackStack() })
        }
        
        composable(NavRoutes.Login.route) {
            val viewModel: MpinLoginViewModel = viewModel(factory = factory)
            val shopName by viewModel.shopName.collectAsState(initial = "")
            val isVerified by viewModel.isVerified.collectAsState()
            MpinLoginScreen(shopName = shopName, onLoginSuccess = { navController.navigate(NavRoutes.Dashboard.route) { popUpTo(NavRoutes.Login.route) { inclusive = true } } }, onVerifyMpin = { viewModel.verifyMpin(it) }, isVerified = isVerified, onResetVerification = { viewModel.resetVerification() }, onForgotPassword = { navController.navigate(NavRoutes.Verify.route) } )
        }

        composable(NavRoutes.Dashboard.route) {
            val viewModel: DashboardViewModel = viewModel(factory = factory)
            DashboardScreen(
                currentRoute = NavRoutes.Dashboard.route,
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true; restoreState = true } },
                onEditProduct = { productId -> navController.navigate(NavRoutes.EditProduct.createRoute(productId)) },
                viewModel = viewModel
            )
        }
        
        composable(NavRoutes.Sell.route) {
            val viewModel: SellViewModel = viewModel(factory = factory)
            SellScreen(currentRoute = NavRoutes.Sell.route, onNavigate = { route -> navController.navigate(route) { launchSingleTop = true; restoreState = true } }, onSaleComplete = { saleId -> navController.navigate(NavRoutes.Bill.createRoute(saleId)) }, viewModel = viewModel)
        }

        composable(NavRoutes.Stock.route) {
            val viewModel: StockViewModel = viewModel(factory = factory)
            StockScreen(
                currentRoute = NavRoutes.Stock.route, 
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true; restoreState = true } }, 
                onAddProduct = { navController.navigate(NavRoutes.AddProduct.route) }, 
                onEditProduct = { productId -> navController.navigate(NavRoutes.EditProduct.createRoute(productId)) },
                viewModel = viewModel
            )
        }

        composable(NavRoutes.AddProduct.route) {
            val viewModel: AddProductViewModel = viewModel(factory = factory)
            AddProductScreen(onBack = { navController.popBackStack() }, viewModel = viewModel)
        }

        composable(NavRoutes.EditProduct.route, arguments = listOf(navArgument("productId") { type = NavType.IntType })) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            val viewModel: AddProductViewModel = viewModel(factory = factory)
            AddProductScreen(productId = productId, onBack = { navController.popBackStack() }, viewModel = viewModel)
        }

        composable(NavRoutes.History.route) {
            val viewModel: HistoryViewModel = viewModel(factory = factory)
            HistoryScreen(currentRoute = NavRoutes.History.route, onNavigate = { route -> navController.navigate(route) { launchSingleTop = true; restoreState = true } }, onViewBill = { saleId -> navController.navigate(NavRoutes.Bill.createRoute(saleId)) }, viewModel = viewModel)
        }

        composable(NavRoutes.Analytics.route) {
            val viewModel: AnalyticsViewModel = viewModel(factory = factory)
            AnalyticsScreen(currentRoute = NavRoutes.Analytics.route, onNavigate = { route -> navController.navigate(route) { launchSingleTop = true; restoreState = true } }, viewModel = viewModel)
        }

        composable(NavRoutes.Bill.route, arguments = listOf(navArgument("saleId") { type = NavType.IntType })) { backStackEntry ->
            val saleId = backStackEntry.arguments?.getInt("saleId") ?: 0
            val billFactory = ViewModelFactory(shopPreferences, productRepository, saleRepository, saleId)
            val viewModel: BillViewModel = viewModel(factory = billFactory)
            BillScreen(saleId = saleId, onBack = { navController.popBackStack() }, viewModel = viewModel)
        }

        composable(NavRoutes.ProductDetails.route, arguments = listOf(navArgument("productId") { type = NavType.IntType })) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            val detailFactory = ViewModelFactory(shopPreferences, productRepository, saleRepository, productId)
            val viewModel: com.example.hasta_kala.ui.screens.details.ProductDetailsViewModel = viewModel(factory = detailFactory)
            com.example.hasta_kala.ui.screens.details.ProductDetailsScreen(productId = productId, onBack = { navController.popBackStack() }, viewModel = viewModel)
        }
        
        composable(NavRoutes.Settings.route) {
            val viewModel: com.example.hasta_kala.ui.screens.profile.ProfileViewModel = viewModel(factory = factory)
            com.example.hasta_kala.ui.screens.profile.ProfileScreen(
                currentRoute = NavRoutes.Settings.route,
                onNavigate = { route -> navController.navigate(route) { launchSingleTop = true; restoreState = true } },
                viewModel = viewModel
            )
        }

        composable("security_verify/{otp}") { backStackEntry ->
            val otp = backStackEntry.arguments?.getString("otp") ?: ""
            OtpScreen(
                expectedOtp = otp, 
                onVerify = { navController.navigate(NavRoutes.ResetMpin.route) }, 
                onBack = { navController.popBackStack() }, 
                onResend = { }
            )
        }

        composable(NavRoutes.ResetMpin.route) {
            val viewModel: SetMpinViewModel = viewModel(factory = factory)
            SetMpinScreen(
                onMpinSet = { mpin ->
                    viewModel.saveMpin(mpin)
                    navController.navigate(NavRoutes.Settings.route) { popUpTo(NavRoutes.Settings.route) { inclusive = true } }
                }, 
                onBack = { navController.popBackStack() }
            )
        }
    }
}
