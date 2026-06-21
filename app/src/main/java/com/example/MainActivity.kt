package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.ui.screens.CareerCoPilotScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ResumeBuilderScreen
import com.example.ui.screens.SavedFilesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.CareerStudioViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Instantiate our central state manager
            val viewModel = ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )[CareerStudioViewModel::class.java]

            MyApplicationTheme {
                var currentTab by remember { mutableIntStateOf(0) }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isWideScreen = maxWidth >= 600.dp

                    Scaffold(
                        modifier = Modifier.fillMaxSize().testTag("app_scaffold"),
                        bottomBar = {
                            if (!isWideScreen) {
                                NavigationBar(
                                    modifier = Modifier.testTag("app_navigation_bar"),
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 8.dp
                                ) {
                                    NavigationBarItem(
                                        selected = currentTab == 0,
                                        onClick = { currentTab = 0 },
                                        label = { Text("Dashboard", style = MaterialTheme.typography.labelSmall) },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == 0) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                                contentDescription = "Dashboard",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        modifier = Modifier.testTag("nav_dashboard")
                                    )

                                    NavigationBarItem(
                                        selected = currentTab == 1,
                                        onClick = { currentTab = 1 },
                                        label = { Text("Resume", style = MaterialTheme.typography.labelSmall) },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == 1) Icons.Filled.Description else Icons.Outlined.Description,
                                                contentDescription = "Builder",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        modifier = Modifier.testTag("nav_builder")
                                    )

                                    NavigationBarItem(
                                        selected = currentTab == 2,
                                        onClick = { currentTab = 2 },
                                        label = { Text("Co-Pilot", style = MaterialTheme.typography.labelSmall) },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == 2) Icons.Filled.Psychology else Icons.Outlined.Psychology,
                                                contentDescription = "Co-Pilot",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        modifier = Modifier.testTag("nav_copilot")
                                    )

                                    NavigationBarItem(
                                        selected = currentTab == 3,
                                        onClick = { currentTab = 3 },
                                        label = { Text("Saved Docs", style = MaterialTheme.typography.labelSmall) },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == 3) Icons.Filled.Book else Icons.Outlined.Book,
                                                contentDescription = "Saved",
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        modifier = Modifier.testTag("nav_saved")
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (isWideScreen) {
                                NavigationRail(
                                    modifier = Modifier.testTag("app_navigation_rail"),
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    header = {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(vertical = 24.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .background(Color(0xFFD1E4FF), CircleShape)
                                                    .clip(CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    "AS",
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF001D36),
                                                    fontSize = 14.sp
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                "Career Studio",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF001D36)
                                            )
                                        }
                                    }
                                ) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    NavigationRailItem(
                                        selected = currentTab == 0,
                                        onClick = { currentTab = 0 },
                                        label = { Text("Dashboard", style = MaterialTheme.typography.labelSmall) },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == 0) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                                contentDescription = "Dashboard"
                                            )
                                        },
                                        modifier = Modifier.testTag("rail_dashboard")
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    NavigationRailItem(
                                        selected = currentTab == 1,
                                        onClick = { currentTab = 1 },
                                        label = { Text("Builder", style = MaterialTheme.typography.labelSmall) },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == 1) Icons.Filled.Description else Icons.Outlined.Description,
                                                contentDescription = "Builder"
                                            )
                                        },
                                        modifier = Modifier.testTag("rail_builder")
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    NavigationRailItem(
                                        selected = currentTab == 2,
                                        onClick = { currentTab = 2 },
                                        label = { Text("Co-Pilot", style = MaterialTheme.typography.labelSmall) },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == 2) Icons.Filled.Psychology else Icons.Outlined.Psychology,
                                                contentDescription = "Co-Pilot"
                                            )
                                        },
                                        modifier = Modifier.testTag("rail_copilot")
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    NavigationRailItem(
                                        selected = currentTab == 3,
                                        onClick = { currentTab = 3 },
                                        label = { Text("Saved Docs", style = MaterialTheme.typography.labelSmall) },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentTab == 3) Icons.Filled.Book else Icons.Outlined.Book,
                                                contentDescription = "Saved"
                                            )
                                        },
                                        modifier = Modifier.testTag("rail_saved")
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .widthIn(max = 1000.dp)
                                ) {
                                    when (currentTab) {
                                        0 -> DashboardScreen(
                                            viewModel = viewModel,
                                            onNavigateToBuilder = { currentTab = 1 },
                                            onNavigateToCoPilot = { currentTab = 2 }
                                        )
                                        1 -> ResumeBuilderScreen(viewModel = viewModel)
                                        2 -> CareerCoPilotScreen(viewModel = viewModel)
                                        3 -> SavedFilesScreen(viewModel = viewModel)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
