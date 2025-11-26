package com.example.hellofigma.appmainpages.register


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellofigma.networkapi.RetrofitClient.retrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.http.Body
import retrofit2.http.POST
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.IOException
import java.net.SocketTimeoutException
import androidx.core.content.edit

// Data classes for request and response
data class SignupRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String
)

data class SignupResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Map<String, String>?
)

// Retrofit API service for signup
interface SignupApiService {
    @POST("api/signup/")
    suspend fun signup(@Body request: SignupRequest): retrofit2.Response<SignupResponse>
}

@Composable
fun RegisterPage(navController: NavController, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign Up",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4741A6)
            )
            Text(
                text = "Create your account",
                fontSize = 16.sp,
                color = Color(0xFF4741A6)
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it.trim() },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            OutlinedTextField(
                value = userId,
                onValueChange = { userId = it.trim() },
                label = { Text("User ID (Email)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                enabled = !isLoading
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = !isLoading
            )
            if (isLoading) {
                CircularProgressIndicator()
            }
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Button(
                onClick = {
                    when {
                        userId.isBlank() || password.isBlank() || name.isBlank() -> {
                            errorMessage = "All fields are required"
                        }
                        !userId.contains("@") || !userId.contains(".") -> {
                            errorMessage = "Please enter a valid email address"
                        }
                        password.length < 6 -> {
                            errorMessage = "Password must be at least 6 characters"
                        }
                        else -> {
                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                val result = signup(userId, password, name, context)
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    when (result) {
                                        is ApiResult.Success -> {
                                            context.getSharedPreferences("DermApp", Context.MODE_PRIVATE)
                                                .edit() {
                                                    putString("user_id", userId)
                                                }
                                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                            navController.navigate("com/example/hellofigma/appmainpages/register/LoginPage.kt") {
                                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                            }
                                        }
                                        is ApiResult.Error -> {
                                            errorMessage = when (result.message) {
                                                "User ID and password required" -> "All fields are required"
                                                "User ID already exists" -> "This email is already registered"
                                                else -> "Signup failed: ${result.message}"
                                            }
                                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4741A6)),
                enabled = !isLoading
            ) {
                Text(
                    text = "Sign Up",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "Already have an account? Sign in",
                fontSize = 14.sp,
                color = Color(0xFF4741A6),
                modifier = Modifier
                    .clickable { navController.navigate("com/example/hellofigma/appmainpages/register/LoginPage.kt") }
                    .padding(top = 8.dp)
            )
        }
    }
}

suspend fun signup(userId: String, password: String, name: String, context: Context): ApiResult {
    return try {
        val response = retrofit.create(SignupApiService::class.java)
            .signup(SignupRequest(userId, password, name))
        if (response.isSuccessful) {
            response.body()?.let { ApiResult.Success(it.message) }
                ?: ApiResult.Error("Signup failed: Empty response")
        } else {
            val errorBody = response.errorBody()?.string()
            val errorMsg = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java).error
                    ?: "Signup failed: ${response.message()}"
            } catch (e: Exception) {
                "Signup failed: ${response.message()}"
            }
            ApiResult.Error(errorMsg)
        }
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
fun RegisterPagePreview() {
    MaterialTheme {
        RegisterPage(navController = androidx.navigation.compose.rememberNavController())
    }
}