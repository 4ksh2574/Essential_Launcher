package com.axo.essentiallauncher

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class LaunchableApp(
    val label: String,
    val packageName: String,
    val icon: android.graphics.drawable.Drawable
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EssentialLauncherApp() }
    }
}

@Composable
private fun EssentialLauncherApp() {
    val context = LocalContext.current
    val packageManager = context.packageManager
    var drawerOpen by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var apps by remember { mutableStateOf(emptyList<LaunchableApp>()) }

    fun loadApps() {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        @Suppress("DEPRECATION")
        apps = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .mapNotNull { info ->
                val activityInfo = info.activityInfo ?: return@mapNotNull null
                if (activityInfo.packageName == context.packageName) return@mapNotNull null
                LaunchableApp(
                    label = info.loadLabel(packageManager).toString(),
                    packageName = activityInfo.packageName,
                    icon = info.loadIcon(packageManager)
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }
    }

    LaunchedEffect(Unit) { loadApps() }

    val filteredApps = apps.filter { it.label.contains(query, ignoreCase = true) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Color(0xFF101114),
            surface = Color(0xFF1C1D21),
            onBackground = Color(0xFFF4F1F8),
            onSurface = Color(0xFFF4F1F8),
            primary = Color(0xFFD7D2E2)
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF101114))
                .pointerInput(drawerOpen) {
                    var totalDrag = 0f
                    detectVerticalDragGestures(
                        onVerticalDrag = { _, amount -> totalDrag += amount },
                        onDragEnd = {
                            if (totalDrag < -55f) drawerOpen = true
                            if (totalDrag > 55f) drawerOpen = false
                            totalDrag = 0f
                        }
                    )
                }
        ) {
            AnimatedVisibility(
                visible = !drawerOpen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                HomeSurface(
                    apps = apps,
                    onOpenDrawer = { drawerOpen = true },
                    onLaunch = { launchApp(context, it.packageName) }
                )
            }

            AnimatedVisibility(
                visible = drawerOpen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xFF101114))
                        .padding(horizontal = 22.dp)
                ) {
                    Spacer(Modifier.height(54.dp))
                    Text(
                        "Search your phone",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(18.dp))
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        placeholder = { Text("Search apps") },
                        shape = RoundedCornerShape(30.dp)
                    )
                    Spacer(Modifier.height(18.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        verticalArrangement = Arrangement.spacedBy(22.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(filteredApps, key = { it.packageName }) { app ->
                            AppTile(app) { launchApp(context, app.packageName) }
                        }
                    }
                    TextButton(
                        onClick = {
                            drawerOpen = false
                            query = ""
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Close app drawer")
                    }
                    Spacer(Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
private fun HomeSurface(
    apps: List<LaunchableApp>,
    onOpenDrawer: () -> Unit,
    onLaunch: (LaunchableApp) -> Unit
) {
    val time = remember { SimpleDateFormat("h:mm", Locale.getDefault()) }
    val date = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(64.dp))
        Text(
            time.format(Date()),
            color = Color(0xFFF5F2F8),
            fontSize = 58.sp,
            fontWeight = FontWeight.Light
        )
        Text(
            date.format(Date()),
            color = Color(0xFFD0CDD5),
            fontSize = 15.sp
        )
        Spacer(Modifier.height(28.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF25262B),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    "At a Glance",
                    color = Color(0xFFF4F1F8),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "Your day, at a glance",
                    color = Color(0xFFBDB9C4),
                    fontSize = 13.sp
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFF25262B))
                .clickable { onOpenDrawer() }
                .padding(horizontal = 18.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFFE5E1EA)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "Search your phone",
                color = Color(0xFFD5D1DA),
                fontSize = 15.sp
            )
        }
        Spacer(Modifier.height(18.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 22.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            apps.take(5).forEach { app ->
                Box(
                    Modifier
                        .size(52.dp)
                        .clickable { onLaunch(app) },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberDrawablePainter(app.icon),
                        contentDescription = app.label,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AppTile(app: LaunchableApp, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = rememberDrawablePainter(app.icon),
            contentDescription = app.label,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(7.dp))
        Text(
            app.label,
            color = Color(0xFFF1EEF5),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun rememberDrawablePainter(
    drawable: android.graphics.drawable.Drawable
): androidx.compose.ui.graphics.painter.Painter {
    val bitmap = remember(drawable) {
        val width = drawable.intrinsicWidth.coerceAtLeast(1)
        val height = drawable.intrinsicHeight.coerceAtLeast(1)
        val image = android.graphics.Bitmap.createBitmap(
            width,
            height,
            android.graphics.Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(image)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        image
    }
    return androidx.compose.ui.graphics.painter.BitmapPainter(bitmap.asImageBitmap())
}

private fun launchApp(context: android.content.Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
