package com.example.hellofigma

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.example.hellofigma.apptools.webview.WebViewer
import com.example.hellofigma.appmainpages.register.LoginPage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloFigmaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavHost(navController)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "openingPage") {
        composable("openingPage")       { AppOpeningPage(navController) }
        composable("loginPage")         { LoginPage(navController) }
        composable("registerPage")      { RegisterPage(navController) }
        composable("homePage")          { Home(navController) }
        composable("miniMarketPage")    { Minimarket(navController) }
        composable("personalInfo")      { PersonalInfo(navController) }
        composable("statsPlan")         { StatsPlan(navController) }
        composable("results")           { Results(navController) }
        composable("progTrack")         { ProgTrack(navController) }
        composable("darkSpots")         { Dark(navController) }
        composable("acne")              { Acne(navController) }
        composable("eczema")            { Eczema(navController) }
        composable("wrinkles")          { Wrinkles(navController) }
        composable("cameraDetection")   { CamScreen(navController) }
        composable("settings")          { Settings(navController) }
        composable("search")            { SearchPage(navController) }
        composable("chatBot")           { DoryVisionWebViewPage(navController) }
        composable("wishlist")          { Wishlist(navController) }
        composable("webViewer")         { WebViewer(navController) }
    }
}
