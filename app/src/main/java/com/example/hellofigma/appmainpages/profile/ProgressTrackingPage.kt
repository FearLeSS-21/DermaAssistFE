package com.example.hellofigma.appmainpages.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import com.example.hellofigma.networkapi.*
import com.example.hellofigma.ui.theme.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import retrofit2.http.GET
import java.text.SimpleDateFormat
import java.util.*

data class SkinScan(val date: String, val detections: Detections)
data class Detections(val acne: Int, val wrinkles: Int, val eczema: Int, val eyebags: Int)
data class ErrorResponse(val error: String?)

interface SkinScanApiService {
    @GET("api/reports/")
    suspend fun getSkinScans(): retrofit2.Response<List<SkinScan>>
}

object DateUtils {
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private val displayDateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
    private val displayTimeFormat = SimpleDateFormat("hh:mm a", Locale.US)

    fun formatForDisplayDate(dateString: String): String = try {
        apiDateFormat.parse(dateString)?.let { displayDateFormat.format(it) } ?: dateString
    } catch (e: Exception) { dateString }

    fun formatForDisplayTime(dateString: String): String = try {
        apiDateFormat.parse(dateString)?.let { date ->
            val calendar = Calendar.getInstance().apply { time = date; add(Calendar.HOUR_OF_DAY, 3) }
            displayTimeFormat.format(calendar.time)
        } ?: ""
    } catch (e: Exception) { "" }

    fun parseApiDate(dateString: String): Date? = try { apiDateFormat.parse(dateString) } catch (e: Exception) { null }
    fun formatForApi(date: Date): String = apiDateFormat.format(date)
}

suspend fun fetchSkinScans(): ApiResult<List<SkinScan>> = try {
    val response = RetrofitClient.retrofit.create(SkinScanApiService::class.java).getSkinScans()
    if (response.isSuccessful) {
        response.body()?.let { ApiResult.Success(it) } ?: ApiResult.Error("Empty response")
    } else {
        val message = response.errorBody()?.string()?.let {
            try { Gson().fromJson(it, ErrorResponse::class.java).error ?: "Failed: ${response.code()}" } catch (e: Exception) { "Failed: ${response.code()}" }
        } ?: "Unknown error"
        ApiResult.Error(message)
    }
} catch (e: Exception) {
    ApiResult.Error(
        when (e) {
            is java.net.SocketTimeoutException -> "Request timed out. Please try again."
            is java.io.IOException -> "Network error: ${e.message}"
            else -> "Unexpected error: ${e.message}"
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgTrack(navController: NavController) {
    var scanData by remember { mutableStateOf<List<SkinScan>>(emptyList()) }
    var filteredScans by remember { mutableStateOf<List<SkinScan>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showGraph by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            loadScans {
                scanData = it
                filteredScans = it
                isLoading = false
                errorMessage = null
            }
        }
        while (true) {
            delay(60000)
            coroutineScope.launch {
                loadScans {
                    scanData = it
                    if (searchQuery.isEmpty()) filteredScans = it
                    else filterScans(it, searchQuery) { filtered -> filteredScans = filtered }
                }
            }
        }
    }

    LaunchedEffect(searchQuery) {
        filterScans(scanData, searchQuery) { filteredScans = it }
    }

    HelloFigmaTheme {
        Scaffold(
            bottomBar = { ReusableBottomNavigationBar(navController) },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                // Header with icons
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Progress Tracking",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { showSearch = !showSearch; if (!showSearch) searchQuery = "" }) {
                            Icon(
                                imageVector = if (showSearch) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Primary
                            )
                        }
                        IconButton(onClick = { showGraph = !showGraph }) {
                            Icon(
                                imageVector = if (showGraph) Icons.Default.ArrowBack else Icons.Default.ShowChart,
                                contentDescription = "Graph",
                                tint = Primary
                            )
                        }
                    }
                }

                // Search bar
                if (showSearch) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        placeholder = { Text("Search by date (e.g., November, 2025)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = GrayText.copy(alpha = 0.3f)
                        )
                    )
                }

                when {
                    isLoading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                    errorMessage != null -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(errorMessage ?: "Unknown error", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    }
                    showGraph -> GraphSection(scanData)
                    else -> ScanListSection(filteredScans)
                }
            }
        }
    }
}

private suspend fun loadScans(onSuccess: (List<SkinScan>) -> Unit) {
    when (val result = fetchSkinScans()) {
        is ApiResult.Success -> onSuccess(result.data.sortedByDescending { DateUtils.parseApiDate(it.date)?.time ?: 0 }.take(30))
        is ApiResult.Error -> {}
    }
}

private fun filterScans(scans: List<SkinScan>, query: String, onResult: (List<SkinScan>) -> Unit) {
    if (query.isEmpty()) {
        onResult(scans)
    } else {
        val filtered = scans.filter { scan ->
            DateUtils.formatForDisplayDate(scan.date).contains(query, ignoreCase = true) ||
                    DateUtils.formatForDisplayTime(scan.date).contains(query, ignoreCase = true) ||
                    scan.date.contains(query, ignoreCase = true)
        }
        onResult(filtered)
    }
}

@Composable
fun ScanListSection(scans: List<SkinScan>) {
    if (scans.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No scans found", color = GrayText, fontSize = 16.sp)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(scans) { scan ->
                val index = scans.indexOf(scan)
                DiagnosisCard(
                    scan = scan,
                    isLatest = scan == scans.first(),
                    alignRight = index % 2 != 0
                )
            }
        }
    }
}

@Composable
fun DiagnosisCard(scan: SkinScan, isLatest: Boolean, alignRight: Boolean = false) {
    var isExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (alignRight) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .shadow(6.dp, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = if (isLatest)
                                listOf(Primary, LinkBlue)
                            else
                                listOf(White, Color(0xFFF5F5F5))
                        )
                    )
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                DateUtils.formatForDisplayDate(scan.date),
                                color = if (isLatest) White else GrayText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                DateUtils.formatForDisplayTime(scan.date),
                                color = if (isLatest) White.copy(alpha = 0.8f) else GrayText.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }
                        if (isLatest) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "Latest",
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(
                            color = if (isLatest) White.copy(alpha = 0.3f) else GrayText.copy(alpha = 0.2f),
                            thickness = 1.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetectionRow("Acne", scan.detections.acne, isLatest)
                            DetectionRow("Wrinkles", scan.detections.wrinkles, isLatest)
                            DetectionRow("Eczema", scan.detections.eczema, isLatest)
                            DetectionRow("Eye Bags", scan.detections.eyebags, isLatest)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetectionRow(label: String, value: Int, isLatest: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = if (isLatest) White.copy(alpha = 0.9f) else GrayText,
            fontSize = 14.sp
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isLatest) White.copy(alpha = 0.2f) else Primary.copy(alpha = 0.1f)
        ) {
            Text(
                value.toString(),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                color = if (isLatest) White else Primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun GraphSection(scans: List<SkinScan>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(White, RoundedCornerShape(16.dp))
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .padding(16.dp)
            ) {
                Text("Trend Analysis", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                Spacer(modifier = Modifier.height(8.dp))
                LineGraph(scans.take(10))
                Spacer(modifier = Modifier.height(12.dp))
                LegendRow()
            }
        }
        item {
            Column(modifier = Modifier
                .fillMaxWidth()
                .background(White, RoundedCornerShape(16.dp))
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .padding(16.dp)
            ) {
                Text("Condition Breakdown", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Primary)
                Spacer(modifier = Modifier.height(8.dp))
                BarChart(scans.take(10))
                Spacer(modifier = Modifier.height(12.dp))
                LegendRow()
            }
        }
    }
}

@Composable
fun LegendRow() {
    val labels = listOf("Acne", "Wrinkles", "Eczema", "Eye Bags")
    val colors = listOf(Primary, LinkBlue, GrayText, LightGray)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        labels.forEachIndexed { index, label ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(colors[index], RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, fontSize = 11.sp, color = GrayText)
            }
        }
    }
}

@Composable
fun LineGraph(scans: List<SkinScan>, modifier: Modifier = Modifier) {
    val colors = listOf(Primary, LinkBlue, GrayText, LightGray)
    val maxValue = 20f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val width = size.width - 60f
        val height = size.height - 60f
        val stepX = if (scans.size > 1) width / (scans.size - 1) else 0f

        listOf(0.25f, 0.5f, 0.75f).forEach { y ->
            drawLine(
                color = GrayText.copy(alpha = 0.2f),
                start = Offset(30f, height * y + 20f),
                end = Offset(width + 30f, height * y + 20f),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )
        }

        val detectionLists = listOf(
            scans.map { it.detections.acne.toFloat() },
            scans.map { it.detections.wrinkles.toFloat() },
            scans.map { it.detections.eczema.toFloat() },
            scans.map { it.detections.eyebags.toFloat() }
        )

        detectionLists.forEachIndexed { index, values ->
            val path = Path()
            values.forEachIndexed { i, value ->
                val x = i * stepX + 30f
                val y = height - (value / maxValue) * height + 20f
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                drawCircle(color = colors[index], radius = 6f, center = Offset(x, y))
            }
            drawPath(
                path = path,
                color = colors[index],
                style = Stroke(width = 3f)
            )
        }
    }
}

@Composable
fun BarChart(scans: List<SkinScan>, modifier: Modifier = Modifier) {
    val colors = listOf(Primary, LinkBlue, GrayText, LightGray)
    val maxValue = 20f

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        val width = size.width - 60f
        val height = size.height - 60f
        val groupWidth = if (scans.isNotEmpty()) width / scans.size else 0f
        val barWidth = (groupWidth / 5).coerceAtMost(25f)
        val spacing = barWidth / 4

        scans.forEachIndexed { i, scan ->
            val x = i * groupWidth + 30f
            listOf(scan.detections.acne, scan.detections.wrinkles, scan.detections.eczema, scan.detections.eyebags)
                .forEachIndexed { j, value ->
                    val barHeight = (value / maxValue) * height
                    drawRoundRect(
                        color = colors[j],
                        topLeft = Offset(x + j * (barWidth + spacing), height - barHeight + 20f),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }
        }
    }
}