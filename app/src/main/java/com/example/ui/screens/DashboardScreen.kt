package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Resume
import com.example.ui.viewmodel.CareerStudioViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: CareerStudioViewModel,
    onNavigateToBuilder: () -> Unit,
    onNavigateToCoPilot: () -> Unit
) {
    val resumes by viewModel.resumesList.collectAsState()
    val activeResume by viewModel.activeResumeState.collectAsState()
    val savedDocs by viewModel.savedDocsList.collectAsState()
    val isAnalyzing by viewModel.isAtsAnalyzing.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newResumeTitle by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Sleek Header (As per HTML header)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, ${activeResume?.name?.ifEmpty { "Alex" } ?: "Alex"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF64748B),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "AI Career Studio",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001D36)
                    )
                }

                // Initial Avatar in Circle
                val initials = if (activeResume?.name?.isNotBlank() == true) {
                    activeResume!!.name.trim().split("\\s+".toRegex()).take(2)
                        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                        .joinToString("")
                } else {
                    "AS"
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFD1E4FF), CircleShape)
                        .clip(CircleShape)
                        .testTag("profile_avatar"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials.take(2),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF001D36),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // 2. Active Profile Selector (Styled with neat light borders and elevated spacing)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Resume Profile",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF001D36)
                    )

                    // Compact "New Resume" button
                    TextButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.testTag("btn_new_resume_profile")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New Profile", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Profile", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (resumes.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECEEF5))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = "No Profile",
                                modifier = Modifier.size(40.dp),
                                tint = Color(0xFF005CB9)
                            )
                            Text(
                                "No resume profiles created.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF001D36)
                            )
                            Button(
                                onClick = { showCreateDialog = true },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Create First Profile", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECEEF5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = activeResume?.title ?: "Select a Profile",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF001D36)
                                    )
                                    Text(
                                        text = if (activeResume?.name?.isNotEmpty() == true) "Candidate: ${activeResume?.name}" else "Tap Edit Profile to set your details",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                IconButton(
                                    onClick = onNavigateToBuilder,
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFE7F1FF)),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit Profile",
                                        tint = Color(0xFF005CB9),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Profile Switch dropdown
                            var expandedDropdown by remember { mutableStateOf(false) }
                            Box {
                                Button(
                                    onClick = { expandedDropdown = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF1F3F9),
                                        contentColor = Color(0xFF001D36)
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Text("Switch Profile (${resumes.size} available)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", modifier = Modifier.size(16.dp))
                                }

                                DropdownMenu(
                                    expanded = expandedDropdown,
                                    onDismissRequest = { expandedDropdown = false }
                                ) {
                                    resumes.forEach { r ->
                                        DropdownMenuItem(
                                            text = { Text(r.title, fontSize = 13.sp) },
                                            onClick = {
                                                viewModel.setActiveResume(r.id)
                                                expandedDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. ATS Health Score Gradient Card (Representing HTML health score card)
        activeResume?.let { resume ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF005CB9), Color(0xFF004289))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "Resume Health Score",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFD1E4FF)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${resume.atsScore}/100",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }

                                // Status pill overlay
                                val badgeText = if (resume.atsScore >= 80) "ATS Ready" else if (resume.atsScore >= 50) "Improving" else "Needs Check"
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = badgeText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your ${resume.title.ifEmpty { "Software Engineer" }} profile details are analyzed against modern recruiter parsing standards.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.padding(end = 36.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { viewModel.analyzeResumeAtsScore() },
                                    enabled = !isAnalyzing,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = Color(0xFF005CB9),
                                        disabledContainerColor = Color.White.copy(alpha = 0.4f),
                                        disabledContentColor = Color(0xFF005CB9)
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                                ) {
                                    if (isAnalyzing) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = Color(0xFF005CB9)
                                        )
                                    } else {
                                        Text("Analyze & Improve", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }

                            // Optional checklist suggestion foldout nested beautifully
                            val suggestions = viewModel.getSuggestions(resume)
                            if (suggestions.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Scans Detected Improvements:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFFD1E4FF)
                                    )
                                    suggestions.take(3).forEach { suggestion ->
                                        Row(
                                            verticalAlignment = Alignment.Top,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.CheckCircle,
                                                contentDescription = "Tip",
                                                modifier = Modifier.size(14.dp).padding(top = 1.dp),
                                                tint = Color.White
                                            )
                                            Text(suggestion, fontSize = 11.sp, color = Color.White.copy(alpha = 0.9f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Feature Grid (Mapped to four specific modules as per Design HTML)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "AI Suite Modules",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF001D36)
                )

                // 2x2 Clean Layout
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardGridItem(
                            title = "Resume Builder",
                            subtitle = "Step-by-step",
                            icon = Icons.Default.Description,
                            iconBgColor = Color(0xFFE7F1FF),
                            iconTint = Color(0xFF005CB9),
                            onClick = onNavigateToBuilder,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardGridItem(
                            title = "Cover Letter",
                            subtitle = "AI Generated",
                            icon = Icons.Default.MailOutline,
                            iconBgColor = Color(0xFFFFF3E7),
                            iconTint = Color(0xFFB95C00),
                            onClick = onNavigateToCoPilot,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardGridItem(
                            title = "Interview AI",
                            subtitle = "Model Answers",
                            icon = Icons.Default.Forum,
                            iconBgColor = Color(0xFFE7FFEF),
                            iconTint = Color(0xFF00B93C),
                            onClick = onNavigateToCoPilot,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardGridItem(
                            title = "Career Advice",
                            subtitle = "Personalized",
                            icon = Icons.Default.Psychology,
                            iconBgColor = Color(0xFFF1E7FF),
                            iconTint = Color(0xFF5C00B9),
                            onClick = onNavigateToCoPilot,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 5. Recent Document Feature (Direct visual replica of HTML Recent Document)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Document",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF001D36)
                    )
                    TextButton(onClick = onNavigateToCoPilot) {
                        Text("View All", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF005CB9))
                    }
                }

                if (savedDocs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F3F9), RoundedCornerShape(20.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No saved generated documents. Use Co-Pilot to draft letters!",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                } else {
                    val latestDoc = savedDocs.first()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F3F9), RoundedCornerShape(20.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Icon overlay shadow
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = "Document Icon",
                                    tint = Color(0xFF005CB9),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = latestDoc.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF001D36),
                                maxLines = 1
                            )
                            Text(
                                text = "AI Generated Custom Doc",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }

                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Saved Indicator",
                            tint = Color(0xFF00B93C),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // 6. Modern Stats Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatItem(
                    label = "Profiles",
                    value = "${resumes.size}",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Saved Files",
                    value = "${savedDocs.size}",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Cost Bar",
                    value = "FREE",
                    color = Color(0xFF00B93C),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Dialog for profile creation
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New Resume Profile", color = Color(0xFF001D36), fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Provide a description for your new resume profile (e.g., Software Engineer, HR Consultant).", fontSize = 13.sp, color = Color(0xFF64748B))
                    OutlinedTextField(
                        value = newResumeTitle,
                        onValueChange = { newResumeTitle = it },
                        label = { Text("Profile Title") },
                        placeholder = { Text("e.g., Senior iOS Developer") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalTitle = newResumeTitle.ifEmpty { "My Career Resume" }
                        viewModel.createEmptyResume(finalTitle)
                        newResumeTitle = ""
                        showCreateDialog = false
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun DashboardGridItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECEEF5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBgColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF001D36),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String,
    color: Color? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECEEF5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color ?: Color(0xFF005CB9)
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
