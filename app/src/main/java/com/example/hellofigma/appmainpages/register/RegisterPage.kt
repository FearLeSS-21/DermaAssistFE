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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellofigma.networkapi.RetrofitClient.retrofit
import com.example.hellofigma.ui.theme.*
import com.example.hellofigma.appmainpages.profile.ErrorResponse
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import androidx.core.content.edit
import retrofit2.http.Body
import retrofit2.http.POST

data class SignupRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String
)

data class SignupResponse(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: Map<String, String>?
)

interface SignupApiService {
    @POST("api/signup/")
    suspend fun signup(@Body request: SignupRequest): retrofit2.Response<SignupResponse>
}

sealed class SignupResult {
    data class Success(val message: String) : SignupResult()
    data class Error(val message: String) : SignupResult()
}

suspend fun signup(userId: String, password: String, name: String): SignupResult {
    return try {
        val response = retrofit.create(SignupApiService::class.java)
            .signup(SignupRequest(userId, password, name))

        if (response.isSuccessful) {
            response.body()?.let { SignupResult.Success(it.message) }
                ?: SignupResult.Error("Empty response from server.")
        } else {
            val errorMsg = try {
                val errorJson = response.errorBody()?.string()
                Gson().fromJson(errorJson, ErrorResponse::class.java).error
            } catch (_: Exception) {
                response.message()
            }
            SignupResult.Error(errorMsg ?: "Unknown signup error")
        }

    } catch (e: Exception) {
        SignupResult.Error(
            when (e) {
                is SocketTimeoutException -> "Timeout! Please try again."
                is IOException -> "Network error: ${e.message}"
                else -> "Unexpected error: ${e.message}"
            }
        )
    }
}

@Composable
fun RegisterPage(navController: NavController, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var userId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LightGray),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("Sign Up", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Primary)
            Text("Create your account", fontSize = 16.sp, color = Primary)

            listOf(
                Triple("Name", name) { v: String -> name = v.trim() },
                Triple("User ID (Email)", userId) { v: String -> userId = v.trim() },
                Triple("Password", password) { v: String -> password = v }
            ).forEach { (label, value, onChange) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = onChange,
                    label = { Text(label, color = GrayText) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = GrayText,
                        focusedLabelColor = Primary,
                    ),
                    visualTransformation =
                        if (label == "Password") PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = when (label) {
                            "User ID (Email)" -> KeyboardType.Email
                            "Password" -> KeyboardType.Password
                            else -> KeyboardType.Text
                        }
                    )
                )
            }

            if (isLoading) CircularProgressIndicator(color = Primary)

            errorMessage?.let {
                Text(it, color = ErrorRed, fontSize = 14.sp)
            }

            Button(
                onClick = {
                    when {
                        name.isBlank() || userId.isBlank() || password.isBlank() ->
                            errorMessage = "All fields are required"
                        !userId.contains("@") || !userId.contains(".") ->
                            errorMessage = "Invalid email format"
                        password.length < 6 ->
                            errorMessage = "Password must be at least 6 characters"
                        else -> {
                            isLoading = true
                            errorMessage = null

                            coroutineScope.launch {
                                val result = signup(userId, password, name)

                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    when (result) {
                                        is SignupResult.Success -> {
                                            context.getSharedPreferences("DermApp", Context.MODE_PRIVATE)
                                                .edit { putString("user_id", userId) }

                                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                            navController.navigate("login") {
                                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                            }
                                        }

                                        is SignupResult.Error -> {
                                            errorMessage = result.message
                                            Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
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
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                enabled = !isLoading
            ) {
                Text("Sign Up", color = White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Text(
                "Already have an account? Sign in",
                fontSize = 14.sp,
                color = LinkBlue,
                modifier = Modifier
                    .clickable { navController.navigate("login") }
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun RegisterPagePreview() {
    HelloFigmaTheme {
        RegisterPage(navController = androidx.navigation.compose.rememberNavController())
    }
}
