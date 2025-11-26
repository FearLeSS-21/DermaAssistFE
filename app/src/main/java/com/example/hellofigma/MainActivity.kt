package com.example.hellofigma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hellofigma.ui.theme.HelloFigmaTheme
import com.example.hellofigma.apptools.wishlist.Wishlist
import com.example.hellofigma.apptools.cameradetection.CamScreen
import com.example.hellofigma.appmainpages.homepage.Home
import com.example.hellofigma.facediseases.acne.Acne
import com.example.hellofigma.facediseases.darkspots.Dark
import com.example.hellofigma.facediseases.eczema.Eczema
import com.example.hellofigma.appmainpages.minimarket.Minimarket
import com.example.hellofigma.appmainpages.register.RegisterPage
import com.example.hellofigma.facediseases.wrinkles.Wrinkles
import com.example.hellofigma.appmainpages.profile.PersonalInfo
import com.example.hellofigma.appmainpages.profile.ProgTrack
import com.example.hellofigma.appmainpages.profile.Results
import com.example.hellofigma.appmainpages.profile.StatsPlan
import com.example.hellofigma.settings.Settings
import com.example.hellofigma.apptools.search.SearchPage
import com.example.hellofigma.appmainpages.openingpage.AppOpeningPage
 // import com.example.hellofigma.apptools.chatbot.DoryVisionBotPage
import com.example.hellofigma.apptools.chatbot.DoryVisionWebViewPage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloFigmaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val navController = rememberNavController()
                    AppNavHost(navController = navController)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "com/example/hellofigma/appmainpages/openingpage/AppOpeningPage.kt") {
        composable("com/example/hellofigma/appmainpages/openingpage/AppOpeningPage.kt")         { AppOpeningPage(navController) }
        composable("com/example/hellofigma/appmainpages/register/LoginPage.kt")                 { com.example.hellofigma.appmainpages.register.LoginPage(navController) }
        composable("com/example/hellofigma/appmainpages/register/RegisterPage.kt")              { RegisterPage(navController) }
        composable("com/example/hellofigma/appmainpages/homepage/HomePage.kt")                  { Home(navController) }
        composable("com/example/hellofigma/appmainpages/minimarket/MiniMarketPage.kt")          { Minimarket(navController) }
        composable("com/example/hellofigma/appmainpages/profile/PersonalDataPage.kt")           { PersonalInfo(navController) }
        composable("com/example/hellofigma/appmainpages/profile/statsPage.kt")                  { StatsPlan(navController) }
        composable("com/example/hellofigma/appmainpages/profile/results.kt")                    { Results(navController) }
        composable("com/example/hellofigma/appmainpages/profile/ProgTrack.kt")                  { ProgTrack(navController) }
        composable("com/example/hellofigma/facediseases/darkspots/DarkEyeSpotsPage.kt")         { Dark(navController) }
        composable("com/example/hellofigma/facediseases/acne/acne.kt")                          { Acne(navController) }
        composable("com/example/hellofigma/facediseases/eczema/EczemaPage.kt")                  { Eczema(navController) }
        composable("com/example/hellofigma/facediseases/wrinkles/WrinklePage.kt")               { Wrinkles(navController) }
        composable("com/example/hellofigma/apptools/cameradetection/CameraDetectionPage.kt")    { CamScreen(navController) }
        composable("com/example/hellofigma/apptools/settings/AppSettingsPage.kt")               { Settings(navController) }
        composable("com/example/hellofigma/apptools/search/SearchPage.kt")                      { SearchPage(navController) }
        composable("com/example/hellofigma/apptools/chatbot/OlamaChatBotPage.kt")               { DoryVisionWebViewPage(navController) }

        composable("com/example/hellofigma/apptools/wishlist/WishlistPage.kt")          { Wishlist(navController) }
        composable("com/example/hellofigma/apptools/webview/WebViewer.kt")              { com.example.hellofigma.apptools.webview.WebViewer(navController) }
    }
}