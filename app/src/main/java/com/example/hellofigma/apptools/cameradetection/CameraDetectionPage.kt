package com.example.hellofigma.apptools.cameradetection

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.hellofigma.ui.theme.*
import kotlinx.coroutines.delay
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val REQUEST_CODE_PERMISSIONS = 10
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

// ——— ALL COLORS NOW FROM THEME ———
private val purpleGradient = Brush.linearGradient(listOf(Primary, PurpleLight))

private val smallButtonModifier = Modifier
    .size(48.dp)
    .clip(CircleShape)
    .background(SemiTransparentBlack)

private val captureButtonModifier = Modifier
    .size(74.dp)                         // Slightly bigger for better UX
    .clip(CircleShape)
    .background(purpleGradient)

private val modernButtonModifier = Modifier
    .width(140.dp)
    .height(48.dp)
    .shadow(8.dp, RoundedCornerShape(24.dp))
    .clip(RoundedCornerShape(24.dp))
    .background(purpleGradient)

@Composable
fun CamScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var processedImage by remember { mutableStateOf<Bitmap?>(null) }
    var showSmallDialog by remember { mutableStateOf(false) }
    var showEnlargedDialog by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }
    var showFailure by remember { mutableStateOf(false) }

    BackHandler {
        when {
            showEnlargedDialog -> showEnlargedDialog = false
            showSmallDialog -> showSmallDialog = false
            else -> navController.popBackStack()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            isProcessing = true
            showFailure = false
            uploadImage(context, it,
                onSuccess = { bitmap ->
                    processedImage = bitmap
                    isProcessing = false
                    showSmallDialog = true
                },
                onFailure = {
                    isProcessing = false
                    showFailure = true
                })
        } ?: showToast(context, "No image selected")
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as ComponentActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        } else {
            startCamera(context, previewView, isFrontCamera, false, lifecycleOwner) { imageCapture = it }
        }
        if (showFailure) {
            delay(5000)
            showFailure = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures(onDoubleTap = { isFrontCamera = !isFrontCamera })
            }
    ) {
        AndroidView(
            factory = { PreviewView(it).apply { previewView = this } },
            modifier = Modifier.fillMaxSize()
        )

        // Rules Dialog
        if (showRulesDialog) {
            Dialog(onDismissRequest = { }) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Camera Rules", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BlackText)
                        Spacer(Modifier.height(12.dp))
                        listOf(
                            "• Center face in frame",
                            "• Use good lighting",
                            "• Keep hair above head",
                            "• Stay still during capture",
                            "• Avoid objects on face",
                            "• No shadows or reflections"
                        ).forEach {
                            Text(it, fontSize = 16.sp, color = BlackText, modifier = Modifier.padding(vertical = 4.dp))
                        }
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = { showRulesDialog = false },
                            modifier = modernButtonModifier,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("Okay", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Processing / Failure Overlay
        if (isProcessing || showFailure) {
            Box(
                modifier = Modifier.fillMaxSize().background(OverlayDark),
                contentAlignment = Alignment.Center
            ) {
                if (isProcessing) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val color by infiniteTransition.animateColor(
                        initialValue = White,
                        targetValue = PurpleLight,
                        animationSpec = infiniteRepeatable(
                            tween(1500, easing = LinearEasing),
                            RepeatMode.Reverse
                        ), label = "pulse"
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = color, modifier = Modifier.size(48.dp), strokeWidth = 5.dp)
                        Spacer(Modifier.height(16.dp))
                        Text("Processing...", color = color, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }
                } else {
                    Text("Failed", color = TrueRed, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Back Button
        AnimatedIconButton(
            onClick = { navController.popBackStack() },
            icon = { Icon(Icons.Default.ArrowBack, "Back", tint = White, modifier = Modifier.size(28.dp)) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(56.dp)
                .shadow(12.dp, CircleShape)
                .background(purpleGradient)
        )

        // Bottom Controls
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showSmallDialog && processedImage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(bottom = 16.dp)
                        .clickable { showEnlargedDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Image(
                        bitmap = processedImage!!.asImageBitmap(),
                        contentDescription = "Preview",
                        modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp))
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery
                AnimatedIconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    icon = { Icon(Icons.Default.PhotoLibrary, "Gallery", tint = White, modifier = Modifier.size(30.dp)) },
                    modifier = smallButtonModifier
                )

                // Capture
                AnimatedIconButton(
                    onClick = {
                        imageCapture?.takePhoto(context, context.filesDir) { uri ->
                            isProcessing = true
                            showFailure = false
                            uploadImage(context, uri,
                                onSuccess = {
                                    processedImage = it
                                    isProcessing = false
                                    showSmallDialog = true
                                },
                                onFailure = {
                                    isProcessing = false
                                    showFailure = true
                                })
                        } ?: showToast(context, "Camera not ready")
                    },
                    icon = { Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(White)) },
                    modifier = captureButtonModifier
                )

                // Switch Camera
                AnimatedIconButton(
                    onClick = { isFrontCamera = !isFrontCamera },
                    icon = { Icon(Icons.Default.Cameraswitch, "Switch", tint = White, modifier = Modifier.size(30.dp)) },
                    modifier = smallButtonModifier
                )
            }
        }

        // Full Result Dialog
        if (showEnlargedDialog && processedImage != null) {
            Dialog(onDismissRequest = { showEnlargedDialog = false }) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    Image(
                        bitmap = processedImage!!.asImageBitmap(),
                        contentDescription = "Result",
                        modifier = Modifier.fillMaxSize().padding(16.dp)
                    )
                    AnimatedIconButton(
                        onClick = { showEnlargedDialog = false },
                        icon = { Icon(Icons.Default.Close, "Close", tint = White, modifier = Modifier.size(32.dp)) },
                        modifier = smallButtonModifier.align(Alignment.TopEnd).padding(16.dp)
                    )
                    Row(
                        modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Button(
                            onClick = {
                                showEnlargedDialog = false
                                navController.navigate("profile/ProgTrack") // adjust route as needed
                            },
                            modifier = modernButtonModifier,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("Go to Results", color = White, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                showEnlargedDialog = false
                                showSmallDialog = false
                                processedImage = null
                            },
                            modifier = modernButtonModifier,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                        ) {
                            Text("Retake", color = White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        icon()
    }
}

// ——— Helper functions (uploadImage, startCamera, etc.) remain unchanged ———
private fun uploadImage(context: Context, imageUri: Uri, onSuccess: (Bitmap) -> Unit, onFailure: () -> Unit) {
    // ... exactly the same as your original
    val client = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .build()
    val executor = Executors.newSingleThreadExecutor()
    executor.execute {
        try {
            val imageBytes = context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                ?: throw IOException("Cannot read image")
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "image.jpg", imageBytes.toRequestBody("image/jpeg".toMediaType()))
                .addFormDataPart("user_id", "default_user")
                .build()
            val request = Request.Builder().url("http://157.245.43.144/api/upload/").post(requestBody).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Server error ${response.code}")
                val json = JSONObject(response.body?.string() ?: throw IOException("Empty body"))
                val url = json.getJSONObject("data").getString("processed_image")
                val bitmap = BitmapFactory.decodeStream(URL(url).openStream())
                Handler(Looper.getMainLooper()).post {
                    onSuccess(bitmap)
                    showToast(context, "Upload Successful")
                }
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                showToast(context, "Error: ${e.message}")
                onFailure()
            }
        } finally {
            executor.shutdownNow()
        }
    }
}

// startCamera, takePhoto, showToast → unchanged (no colors inside)
private fun startCamera(
    context: Context,
    previewView: PreviewView?,
    isFrontCamera: Boolean,
    flashOn: Boolean,
    lifecycleOwner: LifecycleOwner,
    onImageCaptureCreated: (ImageCapture) -> Unit
) {
    ProcessCameraProvider.getInstance(context).addListener({
        try {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val cameraSelector = if (isFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
            val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView?.surfaceProvider) }
            val imageCapture = ImageCapture.Builder()
                .setFlashMode(if (flashOn) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF)
                .build()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
            onImageCaptureCreated(imageCapture)
        } catch (e: Exception) {
            showToast(context, "Camera error: ${e.message}")
        }
    }, ContextCompat.getMainExecutor(context))
}

private fun ImageCapture.takePhoto(context: Context, outputDirectory: File, onPhotoTaken: (Uri) -> Unit) {
    val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    takePicture(outputOptions, ContextCompat.getMainExecutor(context), object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
            onPhotoTaken(Uri.fromFile(photoFile))
            showToast(context, "Photo captured")
        }
        override fun onError(exc: ImageCaptureException) {
            showToast(context, "Capture failed: ${exc.message}")
        }
    })
}

private fun showToast(context: Context, message: String) {
    Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}