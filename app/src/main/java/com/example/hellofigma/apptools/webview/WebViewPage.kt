package com.example.hellofigma.apptools.webview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.hellofigma.BuildConfig
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.example.hellofigma.ui.theme.*   // ← ALL COLORS COME FROM HERE NOW

const val WEBVIEW_URL = "http://157.245.43.144:8501/"
const val SUCCESS_RESULT_CODE = -1

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WebViewer(navController: NavHostController) {
    val context = LocalContext.current
    val webViewState = rememberWebViewState()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val fileChooserLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == SUCCESS_RESULT_CODE) {
            webViewState.filePathCallback?.onReceiveValue(result.data?.data?.let { arrayOf(it) } ?: emptyArray())
        } else {
            webViewState.filePathCallback?.onReceiveValue(null)
        }
        webViewState.filePathCallback = null
    }

    // Camera permission screen
    if (!cameraPermissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            cameraPermissionState.launchPermissionRequest()
        }
        if (!cameraPermissionState.status.isGranted) {
            Box(modifier = Modifier.fillMaxSize().background(White), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Camera permission is required for this feature.", color = BlackText, fontSize = 18.sp)
                    Button(
                        onClick = { cameraPermissionState.launchPermissionRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Grant Permission", color = White)
                    }
                }
            }
            return
        }
    }

    BackHandler(enabled = webViewState.webView?.canGoBack() == true) {
        webViewState.webView?.goBack() ?: navController.popBackStack()
    }

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = errorMessage ?: "Failed to load webpage", color = TrueRed, fontSize = 18.sp)
                    Button(
                        onClick = {
                            errorMessage = null
                            webViewState.webView?.reload()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Retry", color = White)
                    }
                    Button(
                        onClick = { navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = TextSecondary)
                    ) {
                        Text("Go Back", color = White)
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier.weight(1f).background(White),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            webViewState.webView = this
                            settings.apply {
                                javaScriptEnabled = true
                                javaScriptCanOpenWindowsAutomatically = true
                                domStorageEnabled = true
                                databaseEnabled = false
                                allowFileAccess = false
                                allowContentAccess = false
                                setGeolocationEnabled(true)
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                                }
                            }
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                    webViewState.isLoading = true
                                    webViewState.canGoBack = view?.canGoBack() ?: false
                                }
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    webViewState.isLoading = false
                                    webViewState.canGoBack = view?.canGoBack() ?: false
                                    view?.evaluateJavascript(
                                        """
                                        (function() {
                                            var meta = document.createElement('meta');
                                            meta.setAttribute('name', 'viewport');
                                            meta.setAttribute('content', 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no');
                                            document.getElementsByTagName('head')[0].appendChild(meta);
                                        })();
                                        """.trimIndent(),
                                        null
                                    )
                                }
                                override fun onReceivedHttpError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    errorResponse: WebResourceResponse?
                                ) {
                                    errorMessage = "HTTP Error: ${errorResponse?.statusCode} ${errorResponse?.reasonPhrase}"
                                }
                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        errorMessage = "Error: ${error?.description}"
                                    }
                                }
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    if (url.startsWith("http://") || url.startsWith("https://")) {
                                        view?.loadUrl(url)
                                        return true
                                    }
                                    return false
                                }
                            }
                            webChromeClient = object : WebChromeClient() {
                                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                    consoleMessage?.let {
                                        if (it.messageLevel() == ConsoleMessage.MessageLevel.ERROR) {
                                            Toast.makeText(context, "JS Error: ${it.message()}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    return true
                                }
                                override fun onGeolocationPermissionsShowPrompt(
                                    origin: String?,
                                    callback: GeolocationPermissions.Callback?
                                ) {
                                    callback?.invoke(origin, true, false)
                                }
                                override fun onShowFileChooser(
                                    webView: WebView?,
                                    filePathCallback: ValueCallback<Array<Uri>>?,
                                    fileChooserParams: FileChooserParams?
                                ): Boolean {
                                    webViewState.filePathCallback = filePathCallback
                                    val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                        type = "*/*"
                                    }
                                    fileChooserLauncher.launch(intent)
                                    return true
                                }
                            }
                            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
                            if (WEBVIEW_URL.isNotEmpty()) {
                                loadUrl(WEBVIEW_URL)
                            } else {
                                errorMessage = "Invalid URL configuration"
                            }
                            webViewState.savedState?.let { restoreState(it) }
                        }
                    },
                    update = { webView ->
                        val bundle = Bundle()
                        webView.saveState(bundle)
                        webViewState.savedState = bundle
                    }
                )

                // Zoom controls at bottom
                Row(
                    modifier = Modifier

                        .padding(16.dp)
                        .background(CardBackground.copy(alpha = 0.95f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { webViewState.webView?.zoomOut() },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Zoom Out", color = White)
                    }
                    Button(
                        onClick = { webViewState.webView?.zoomIn() },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Zoom In", color = White)
                    }
                }
            }

            if (webViewState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Primary,
                    strokeWidth = 5.dp
                )
            }
        }
    }
}

class WebViewState {
    var webView: WebView? = null
    var isLoading by mutableStateOf(false)
    var canGoBack by mutableStateOf(false)
    var savedState: Bundle? = null
    var filePathCallback by mutableStateOf<ValueCallback<Array<Uri>>?>(null)
}

@Composable
fun rememberWebViewState(): WebViewState = remember { WebViewState() }