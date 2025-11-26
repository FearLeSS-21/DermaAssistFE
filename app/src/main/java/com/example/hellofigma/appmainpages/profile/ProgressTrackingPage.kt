package com.example.hellofigma.appmainpages.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hellofigma.networkapi.ApiResult
import com.example.hellofigma.networkapi.RetrofitClient
import com.example.hellofigma.apptools.navigationbar.ReusableBottomNavigationBar
import com.example.hellofigma.ui.theme.HelloFigmaTheme
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.http.GET
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle

data class SkinScan(
    @SerializedName("date") val date: String,
    @SerializedName("detections") val detections: Detections
)

data class Detections(
    @SerializedName("acne") val acne: Int,
    @SerializedName("wrinkles") val wrinkles: Int,
    @SerializedName("eczema") val eczema: Int,
    @SerializedName("eyebags") val eyebags: Int
)

data class ErrorResponse(@SerializedName("error") val error: String?)

interface SkinScanApiService {
    @GET("api/reports/") suspend fun getSkinScans(): retrofit2.Response<List<SkinScan>>
}

object DateUtils {
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    private val displayDateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
    private val displayTimeFormat = SimpleDateFormat("hh:mm a", Locale.US)

    fun formatForDisplayDate(dateString: String): String = try {
        apiDateFormat.parse(dateString)?.let { displayDateFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }

    fun formatForDisplayTime(dateString: String): String = try {
        apiDateFormat.parse(dateString)?.let { date ->
            val calendar = Calendar.getInstance().apply {
                time = date
                add(Calendar.HOUR_OF_DAY, 3)
            }
            displayTimeFormat.format(calendar.time)
        } ?: ""
    } catch (e: Exception) {
        ""
    }

    fun parseApiDate(dateString: String): Date? = try {
        apiDateFormat.parse(dateString)
    } catch (e: Exception) {
        null
    }

    fun formatForApi(date: Date): String = apiDateFormat.format(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgTrack(navController: NavController) {
    var scanData by remember { mutableStateOf<List<SkinScan>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showLatestOnly by remember { mutableStateOf(false) }
    var showGraph by remember { mutableStateOf(false) }
    var showFullScreenGraph by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var selectedDate by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val result = fetchSkinScans()
            withContext(Dispatchers.Main) {
                isLoading = false
                when (result) {
                    is ApiResult.Success -> scanData = result.data.sortedByDescending {
                        DateUtils.parseApiDate(it.date)?.time ?: 0
                    }.take(30)
                    is ApiResult.Error -> errorMessage = result.message
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60000)
            coroutineScope.launch {
                val result = fetchSkinScans()
                withContext(Dispatchers.Main) {
                    when (result) {
                        is ApiResult.Success -> scanData = result.data.sortedByDescending {
                            DateUtils.parseApiDate(it.date)?.time ?: 0
                        }.take(30)
                        is ApiResult.Error -> errorMessage = result.message
                    }
                }
            }
        }
    }

    HelloFigmaTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFF6F5FF), Color(0xFFE8E7FF))))
                .padding(top = 16.dp)
        ) {
            if (showFullScreenGraph) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = if (isLandscape) 24.dp else 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Skin Analysis Trends",
                            fontSize = if (isLandscape) 30.sp else 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3F3D9A)
                        )
                        IconButton(onClick = { showFullScreenGraph = false }, modifier = Modifier.size(40.dp)) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color(0xFF3F3D9A))
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Trend Analysis",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF3F3D9A),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            LineGraph(
                                scans = scanData.take(5),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Condition Breakdown",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF3F3D9A),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            BarChart(
                                scans = scanData.take(5),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Progress Tracking",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3F3D9A)
                        )
                        IconButton(
                            onClick = { showGraph = !showGraph },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (showGraph) Icons.Default.ArrowBack else Icons.Default.ShowChart,
                                contentDescription = if (showGraph) "Back" else "Show Graph",
                                tint = Color(0xFF3F3D9A)
                            )
                        }
                    }

                    if (!showGraph) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showDatePicker = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .shadow(4.dp, RoundedCornerShape(22.dp)),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F3D9A))
                            ) {
                                Text("Track by Date", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = {
                                    showDatePicker = false
                                    selectedDate = null
                                    showLatestOnly = !showLatestOnly
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .shadow(4.dp, RoundedCornerShape(22.dp)),
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (showLatestOnly) Color(0xFF4CAF50) else Color(0xFF3F3D9A)
                                )
                            ) {
                                Text("Latest", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (showGraph) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(Color.White, RoundedCornerShape(16.dp))
                                .shadow(4.dp, RoundedCornerShape(16.dp))
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                                    .clickable { showFullScreenGraph = true }
                            ) {
                                Text(
                                    text = "Trend Analysis",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF3F3D9A),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                LineGraph(
                                    scans = scanData.take(5),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                                    .clickable { showFullScreenGraph = true }
                            ) {
                                Text(
                                    text = "Condition Breakdown",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF3F3D9A),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                BarChart(
                                    scans = scanData.take(5),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                        }
                    } else {
                        when {
                            isLoading -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color(0xFF3F3D9A), strokeWidth = 4.dp)
                                }
                            }
                            errorMessage != null -> {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = errorMessage ?: "Unknown error",
                                        color = Color(0xFFD32F2F),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            else -> {
                                val filtered = scanData.filter { scan ->
                                    selectedDate?.let { selected ->
                                        DateUtils.parseApiDate(scan.date)?.let { scanDate ->
                                            DateUtils.parseApiDate(selected)?.let { selectedDateParsed ->
                                                val scanCal = Calendar.getInstance().apply { time = scanDate }
                                                val selectedCal = Calendar.getInstance().apply { time = selectedDateParsed }
                                                scanCal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) &&
                                                        scanCal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) &&
                                                        scanCal.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH)
                                            } ?: false
                                        } ?: false
                                    } ?: true
                                }.let { scans -> if (showLatestOnly && scans.isNotEmpty()) listOf(scans.first()) else scans }

                                if (filtered.isEmpty()) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = if (selectedDate != null) "No scans found for ${DateUtils.formatForDisplayDate(selectedDate ?: "")}" else "No scans found",
                                            color = Color(0xFF555555),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        contentPadding = PaddingValues(vertical = 12.dp)
                                    ) {
                                        items(filtered) { scan ->
                                            DiagnosisCard(scan, showLatestOnly || scan == scanData.firstOrNull())
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (showDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val date = Date(millis)
                                        val calendar = Calendar.getInstance().apply {
                                            time = date
                                            set(Calendar.HOUR_OF_DAY, 0)
                                            set(Calendar.MINUTE, 0)
                                            set(Calendar.SECOND, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }
                                        selectedDate = DateUtils.formatForApi(calendar.time)
                                    }
                                    showDatePicker = false
                                }) { Text("OK", color = Color(0xFF3F3D9A)) }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    showDatePicker = false
                                    selectedDate = null
                                }) { Text("Clear Filter", color = Color(0xFF3F3D9A)) }
                            }
                        ) {
                            DatePicker(state = datePickerState, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }

            ReusableBottomNavigationBar(navController = navController)
        }
    }
}

@Composable
fun DiagnosisCard(scan: SkinScan, isLatest: Boolean) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(
                    colors = if (isLatest) listOf(Color(0xFF4CAF50), Color(0xFF66BB6A)) else listOf(Color(0xFFFFFFFF), Color(0xFFF8F9FA))
                ))
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp)
        ) {
            Column {
                Column {
                    Text(
                        text = DateUtils.formatForDisplayDate(scan.date),
                        color = if (isLatest) Color.White else Color(0xFF333333),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = DateUtils.formatForDisplayTime(scan.date),
                        color = if (isLatest) Color.White.copy(alpha = 0.8f) else Color(0xFF555555),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(
                    color = if (isLatest) Color.White.copy(alpha = 0.7f) else Color(0xFFE0E0E0),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                if (isExpanded) {
                    Column {
                        Text("Acne: ${scan.detections.acne}", color = if (isLatest) Color.White else Color(0xFF555555), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text("Wrinkles: ${scan.detections.wrinkles}", color = if (isLatest) Color.White else Color(0xFF555555), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text("Eczema: ${scan.detections.eczema}", color = if (isLatest) Color.White else Color(0xFF555555), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        Text("Eye Bags: ${scan.detections.eyebags}", color = if (isLatest) Color.White else Color(0xFF555555), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@Composable
fun LineGraph(scans: List<SkinScan>, modifier: Modifier = Modifier) {
    val maxValue = 20f
    val colors = listOf(
        Color(0xFFE91E63), // Pink
        Color(0xFF2196F3), // Blue
        Color(0xFFFFC107), // Amber
        Color(0xFF9C27B0)  // Purple
    )

    Canvas(modifier = modifier.background(Color.White, RoundedCornerShape(12.dp))) {
        val width = size.width - 60f // Add padding for labels
        val height = size.height - 60f // Add padding for labels
        val stepX = if (scans.isNotEmpty()) width / (scans.size.coerceAtLeast(1) - 1).coerceAtLeast(1) else 0f

        // Draw background grid
        listOf(0.25f, 0.5f, 0.75f).forEach { y ->
            drawLine(
                color = Color(0xFFE0E0E0),
                start = Offset(40f, height * y + 20f),
                end = Offset(width + 40f, height * y + 20f),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }

        // Draw Y-axis labels
        listOf(0f, 5f, 10f, 15f, 20f).forEach { value ->
            val y = height - (value / maxValue) * height + 20f
            drawContext.canvas.nativeCanvas.drawText(
                value.toInt().toString(),
                10f,
                y + 8f,
                android.graphics.Paint().apply {
                    color = Color(0xFF666666).toArgb()
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }

        // Draw lines and points
        listOf(
            scans.map { it.detections.acne.toFloat() },
            scans.map { it.detections.wrinkles.toFloat() },
            scans.map { it.detections.eczema.toFloat() },
            scans.map { it.detections.eyebags.toFloat() }
        ).forEachIndexed { index, values ->
            val path = Path()
            values.forEachIndexed { i, value ->
                val x = i * stepX + 40f
                val y = height - (value / maxValue) * height + 20f
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                drawCircle(
                    color = colors[index],
                    radius = 6f,
                    center = Offset(x, y)
                )
                drawContext.canvas.nativeCanvas.drawText(
                    value.toInt().toString(),
                    x + 8f,
                    y - 8f,
                    android.graphics.Paint().apply {
                        color = colors[index].toArgb()
                        textSize = 24f
                        textAlign = android.graphics.Paint.Align.LEFT
                    }
                )
            }
            drawPath(
                path = path,
                color = colors[index],
                style = Stroke(
                    width = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 8f), 0f)
                )
            )
        }

        // Draw X-axis labels
        scans.forEachIndexed { index, scan ->
            val x = index * stepX + 40f
            drawContext.canvas.nativeCanvas.drawText(
                DateUtils.formatForDisplayDate(scan.date).substring(0, 6),
                x,
                height + 40f,
                android.graphics.Paint().apply {
                    color = Color(0xFF666666).toArgb()
                    textSize = 20f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("Acne", "Wrinkles", "Eczema", "Eye Bags").forEachIndexed { index, label ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(colors[index], RoundedCornerShape(4.dp))
                )
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}

@Composable
fun BarChart(scans: List<SkinScan>, modifier: Modifier = Modifier) {
    val maxValue = 20f
    val colors = listOf(
        Color(0xFFE91E63),
        Color(0xFF2196F3),
        Color(0xFFFFC107),
        Color(0xFF9C27B0)
    )

    Canvas(modifier = modifier.background(Color.White, RoundedCornerShape(12.dp))) {
        val width = size.width - 60f // Add padding for labels
        val height = size.height - 60f // Add padding for labels
        val groupWidth = if (scans.isNotEmpty()) width / scans.size else 0f
        val barWidth = groupWidth / 5 // 4 bars plus spacing
        val spacing = barWidth / 4

        // Draw background grid
        listOf(0.25f, 0.5f, 0.75f).forEach { y ->
            drawLine(
                color = Color(0xFFE0E0E0),
                start = Offset(40f, height * y + 20f),
                end = Offset(width + 40f, height * y + 20f),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }

        // Draw Y-axis labels
        listOf(0f, 5f, 10f, 15f, 20f).forEach { value ->
            val y = height - (value / maxValue) * height + 20f
            drawContext.canvas.nativeCanvas.drawText(
                value.toInt().toString(),
                10f,
                y + 8f,
                android.graphics.Paint().apply {
                    color = Color(0xFF666666).toArgb()
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }

        // Draw bars
        scans.forEachIndexed { i, scan ->
            val x = i * groupWidth + 40f
            listOf(
                scan.detections.acne.toFloat(),
                scan.detections.wrinkles.toFloat(),
                scan.detections.eczema.toFloat(),
                scan.detections.eyebags.toFloat()
            ).forEachIndexed { j, value ->
                val barHeight = (value / maxValue) * height
                val barX = x + j * (barWidth + spacing)
                drawRect(
                    color = colors[j],
                    topLeft = Offset(barX, height - barHeight + 20f),
                    size = Size(barWidth, barHeight),
                    style = Stroke(width = 2f)
                )
                drawRect(
                    color = colors[j].copy(alpha = 0.3f),
                    topLeft = Offset(barX, height - barHeight + 20f),
                    size = Size(barWidth, barHeight)
                )
                drawContext.canvas.nativeCanvas.drawText(
                    value.toInt().toString(),
                    barX + barWidth / 2,
                    height - barHeight + 12f,
                    android.graphics.Paint().apply {
                        color = colors[j].toArgb()
                        textSize = 20f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }

        // Draw X-axis labels
        scans.forEachIndexed { index, scan ->
            val x = index * groupWidth + groupWidth / 2 + 40f
            drawContext.canvas.nativeCanvas.drawText(
                DateUtils.formatForDisplayDate(scan.date).substring(0, 6),
                x,
                height + 40f,
                android.graphics.Paint().apply {
                    color = Color(0xFF666666).toArgb()
                    textSize = 20f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("Acne", "Wrinkles", "Eczema", "Eye Bags").forEachIndexed { index, label ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(colors[index], RoundedCornerShape(4.dp))
                )
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
            }
        }
    }
}

suspend fun fetchSkinScans(): ApiResult<List<SkinScan>> = try {
    val response = RetrofitClient.retrofit.create(SkinScanApiService::class.java).getSkinScans()
    if (response.isSuccessful) {
        response.body()?.let { ApiResult.Success(it) } ?: ApiResult.Error("Empty response")
    } else {
        val errorBody = response.errorBody()?.string()
        val message = errorBody?.let {
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

@Composable
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
fun PreviewProgTrack() {
    HelloFigmaTheme {
        ProgTrack(navController = androidx.navigation.compose.rememberNavController())
    }
}