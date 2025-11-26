
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.hellofigma.BuildConfig
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// Configurable URL (replace with your actual URL in BuildConfig or elsewhere)
const val WEBVIEW_URL = "http://157.245.43.144:8501/"

// Custom constant to replace RESULT_OK
const val SUCCESS_RESULT_CODE = -1

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WebViewer(navController: NavHostController) {
    val context = LocalContext.current
    val webViewState = rememberWebViewState()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // File chooser launcher
    val fileChooserLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == SUCCESS_RESULT_CODE) { // Check if SUCCESS_RESULT_CODE is resolved
            webViewState.filePathCallback?.onReceiveValue(result.data?.data?.let { arrayOf(it) } ?: emptyArray())
        } else {
            webViewState.filePathCallback?.onReceiveValue(null)
        }
        webViewState.filePathCallback = null
    }

    // Handle camera permission
    if (!cameraPermissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            cameraPermissionState.launchPermissionRequest()
        }
        if (!cameraPermissionState.status.isGranted) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Camera permission is required for this feature.")
                    Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            }
            return
        }
    }

    // Handle back navigation
    BackHandler(enabled = webViewState.webView?.canGoBack() == true) {
        webViewState.webView?.goBack() ?: navController.popBackStack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = errorMessage ?: "Failed to load webpage")
                    Button(onClick = {
                        errorMessage = null
                        webViewState.webView?.reload()
                    }) {
                        Text("Retry")
                    }
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier.weight(1f).background(Color.White),
                    factory = { context ->
                        WebView(context).apply {
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
                                setSupportZoom(true) // Enable zoom support
                                builtInZoomControls = true // Enable built-in zoom controls (pinch-to-zoom)
                                displayZoomControls = false // Hide built-in zoom buttons, rely on pinch or custom controls
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
                                    android.util.Log.e("WebView", "HTTP Error: ${errorResponse?.statusCode}")
                                }
                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        errorMessage = "Error: ${error?.description}"
                                        android.util.Log.e("WebView", "Error: ${error?.description}")
                                    }
                                }
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    android.util.Log.d("WebView", "Loading URL: $url")
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
                                        android.util.Log.d(
                                            "WebViewConsole",
                                            "${it.message()} -- From line ${it.lineNumber()} of ${it.sourceId()}"
                                        )
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
                // Custom zoom controls
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { webViewState.webView?.zoomOut() }) {
                        Text("Zoom Out")
                    }
                    Button(onClick = { webViewState.webView?.zoomIn() }) {
                        Text("Zoom In")
                    }
                }
            }
            if (webViewState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
fun rememberWebViewState(): WebViewState {
    return remember { WebViewState() }
}
