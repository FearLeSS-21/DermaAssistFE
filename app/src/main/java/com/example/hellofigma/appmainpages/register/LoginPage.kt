package com.example.hellofigma.appmainpages.register

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellofigma.networkapi.RetrofitClient.retrofit
import com.example.hellofigma.ui.theme.HelloFigmaTheme
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException
import java.net.SocketTimeoutException

sealed class ApiResult {
    data class Success(val message: String) : ApiResult()
    data class Error(val message: String) : ApiResult()
}

data class LoginRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Map<String, String>?
)

data class ErrorResponse(
    @SerializedName("error") val error: String?
)

interface LoginApiService {
    @POST("api/login/")
    suspend fun login(@Body request: LoginRequest): retrofit2.Response<LoginResponse>
}

@Composable
fun LoginPage(navController: NavController, modifier: Modifier = Modifier) {
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) { isVisible = true }

    HelloFigmaTheme {
        Box(
            modifier = modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(animationSpec = tween(1000)),
                exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(animationSpec = tween(1000))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFF4741A6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "D", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    Text(
                        text = "Welcome to Derm",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4741A6),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    )

                    Text(
                        text = "Sign in to your account",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    OutlinedTextField(
                        value = userId,
                        onValueChange = { userId = it },
                        label = { Text("User ID (Email)") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        enabled = !isLoading,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedIndicatorColor = Color(0xFF4741A6),
                            unfocusedIndicatorColor = Color.Gray,
                            focusedLabelColor = Color(0xFF4741A6),
                            cursorColor = Color(0xFF4741A6)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        enabled = !isLoading,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedIndicatorColor = Color(0xFF4741A6),
                            unfocusedIndicatorColor = Color.Gray,
                            focusedLabelColor = Color(0xFF4741A6),
                            cursorColor = Color(0xFF4741A6)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Forgot Password?",
                            fontSize = 14.sp,
                            color = Color(0xFF2A9CEE),
                            modifier = Modifier.clickable { /* Handle forgot password */ }
                        )
                        Text(
                            text = "Sign Up",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF4741A6),
                            modifier = Modifier.clickable {
                                navController.navigate("com/example/hellofigma/appmainpages/register/RegisterPage.kt")
                            }
                        )
                    }

                    Button(
                        onClick = {
                            when {
                                userId.isBlank() || password.isBlank() -> errorMessage = "User ID and password are required"
                                !userId.contains("@") || !userId.contains(".") -> errorMessage = "Please enter a valid email address"
                                else -> {
                                    isLoading = true
                                    errorMessage = null
                                    coroutineScope.launch {
                                        val result = login(userId, password, context)
                                        withContext(Dispatchers.Main) {
                                            isLoading = false
                                            when (result) {
                                                is ApiResult.Success -> {
                                                    context.getSharedPreferences("DermApp", Context.MODE_PRIVATE)
                                                        .edit()
                                                        .putString("user_id", userId)
                                                        .apply()
                                                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                                    if (result.message == "Login successful" && password == "default_password") {
                                                        Toast.makeText(context, "Please change your default password after login.", Toast.LENGTH_LONG).show()
                                                    }
                                                    navController.navigate("com/example/hellofigma/appmainpages/homepage/HomePage.kt") {
                                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                                    }
                                                }
                                                is ApiResult.Error -> {
                                                    errorMessage = when (result.message) {
                                                        "User ID and password required" -> "User ID and password are required"
                                                        "User does not exist" -> "User does not exist"
                                                        "Incorrect password" -> "Incorrect password"
                                                        else -> result.message
                                                    }
                                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4741A6),
                            contentColor = Color.White
                        ),
                        enabled = !isLoading
                    ) {
                        Text(text = "Sign In", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    }

                    Text(
                        text = "Or sign in with",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(
                            Pair("G", Color(0xFF4741A6)),
                            Pair("F", Color(0xFF3B5998)),
                            Pair("X", Color.Black)
                        ).forEach { (text, color) ->
                            IconButton(onClick = { /* Common action or empty */ }) {
                                Box(
                                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFF5F5F5)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = text, fontSize = 24.sp, color = color)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

suspend fun login(userId: String, password: String, context: Context): ApiResult {
    return try {
        val response = retrofit.create(LoginApiService::class.java).login(LoginRequest(userId, password))
        response.body()?.let { ApiResult.Success(it.message) }
            ?: ApiResult.Error("Login failed: Empty response")
    } catch (e: Exception) {
        ApiResult.Error(
            when (e) {
                is SocketTimeoutException -> "Request timed out. Please try again."
                is IOException -> "Network error: ${e.message}"
                else -> "Unexpected error: ${e.message}"
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPagePreview() {
    MaterialTheme {
        LoginPage(navController = androidx.navigation.compose.rememberNavController())
    }
}