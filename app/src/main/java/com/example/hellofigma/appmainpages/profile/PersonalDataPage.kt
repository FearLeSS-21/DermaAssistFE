package com.example.hellofigma.appmainpages.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfo(navController: NavController, modifier: Modifier = Modifier) {
    val countries = remember { Locale.getISOCountries().map { Locale("", it).displayCountry }.sorted() }
    val languages = listOf("English", "Arabic")

    var selectedCountry by remember { mutableStateOf("Egypt") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedDOB by remember { mutableStateOf("21/08/2003") }

    var showCountryDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showDOBDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Personal Information",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2A2A2A)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF4741A6))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        },
        bottomBar = { ReusableBottomNavigationBar(navController) },
        containerColor = Color(0xFFF5F5F5),
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE6E6E6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Icon",
                        tint = Color(0xFF7B61FF),
                        modifier = Modifier.size(64.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 6.dp, y = 6.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFF4741A6), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Add Photo",
                            tint = Color(0xFF4741A6),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                ProfileStatsTabs(navController)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                ModernTextField("Name", "Zeyad Wael", false) {}
                ModernTextField("Email", "Z@gmail.com", false) {}
                ModernTextField("Password", "********", false) {}
                ModernTextField("Date of Birth", selectedDOB, true) {
                    showDOBDialog = true
                }
                ModernTextField("Country/Region", selectedCountry, true) {
                    showCountryDialog = true
                }
                ModernTextField("Language", selectedLanguage, true) {
                    showLanguageDialog = true
                }
            }

            if (showCountryDialog) {
                AlertDialog(
                    onDismissRequest = { showCountryDialog = false },
                    title = { Text("Select Country") },
                    confirmButton = {},
                    text = {
                        Column(Modifier.height(300.dp).verticalScroll(rememberScrollState())) {
                            countries.forEach { country ->
                                Text(
                                    text = country,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            selectedCountry = country
                                            showCountryDialog = false
                                        }
                                )
                            }
                        }
                    }
                )
            }

            if (showLanguageDialog) {
                AlertDialog(
                    onDismissRequest = { showLanguageDialog = false },
                    title = { Text("Select Language") },
                    confirmButton = {},
                    text = {
                        Column {
                            languages.forEach { lang ->
                                Text(
                                    text = lang,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            selectedLanguage = lang
                                            showLanguageDialog = false
                                        }
                                )
                            }
                        }
                    }
                )
            }

            if (showDOBDialog) {
                var dobInput by remember { mutableStateOf(selectedDOB) }

                AlertDialog(
                    onDismissRequest = { showDOBDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            selectedDOB = dobInput
                            showDOBDialog = false
                        }) {
                            Text("Save")
                        }
                    },
                    title = { Text("Edit Date of Birth") },
                    text = {
                        OutlinedTextField(
                            value = dobInput,
                            onValueChange = { dobInput = it },
                            label = { Text("Date of Birth (dd/MM/yyyy)") },
                            singleLine = true
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun ModernTextField(
    label: String,
    placeholder: String,
    isSelectable: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "scale")
    val elevation by animateFloatAsState(if (isPressed) 2f else 6f, label = "elevation")

    Box(
        modifier = Modifier
            .widthIn(max = 320.dp)
            .height(74.dp)
            .scale(scale)
            .shadow(elevation.dp, RoundedCornerShape(18.dp))
            .background(Color(0xFFF5F2FF), RoundedCornerShape(18.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isSelectable,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontSize = 15.sp,
                    color = Color(0xFF4741A6),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = placeholder,
                    fontSize = 17.sp,
                    color = Color(0xFF534C4C),
                    fontWeight = FontWeight.Medium
                )
            }
            if (isSelectable) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color(0xFF7B61FF),
                    modifier = Modifier
                        .size(20.dp)
                        .padding(start = 8.dp)
                )
            }
        }
    }
}

@Preview(widthDp = 412, heightDp = 865)
@Composable
private fun PersonalInfoPreview() {
    MaterialTheme {
        PersonalInfo(navController = rememberNavController())
    }
}
