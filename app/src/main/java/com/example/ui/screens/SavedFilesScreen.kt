package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SavedDoc
import com.example.ui.viewmodel.CareerStudioViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SavedFilesScreen(viewModel: CareerStudioViewModel) {
    val context = LocalContext.current
    val clipboardManager = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    val savedDocs by viewModel.savedDocsList.collectAsState()

    var expandedDocId by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("saved_docs_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Saved AI Career Files", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF001D36))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Access generated portfolios, cover letters, and interview preparation files offline at zero cost.", fontSize = 12.sp, color = Color(0xFF64748B))
        }

        if (savedDocs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFE7F1FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Description,
                            contentDescription = "Empty Folder",
                            tint = Color(0xFF005CB9),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = "Your Local Folder is Empty",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF001D36)
                    )
                    Text(
                        text = "Any cover letters, roadmaps, portfolios, or prep sets generated inside the AI Career Co-Pilot section can be stored here for easy offline adjustments, with zero cost barriers.",
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedDocs, key = { it.id }) { doc ->
                    val isExpanded = expandedDocId == doc.id
                    val dateFormatted = remember(doc.timestamp) {
                        SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault()).format(Date(doc.timestamp))
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedDocId = if (isExpanded) null else doc.id },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpanded) Color(0xFFE7F1FF) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isExpanded) Color(0xFF005CB9).copy(alpha = 0.3f) else Color(0xFFECEEF5)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Head rows
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Category Tag
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(getCategoryColor(doc.type).copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = getCategoryName(doc.type),
                                        color = getCategoryColor(doc.type),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.deleteDocById(doc.id)
                                        Toast.makeText(context, "Deleted saved document successfully.", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = doc.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "Saved: $dateFormatted",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (isExpanded) {
                                Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                                
                                Text(
                                    text = doc.content,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                 Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            clipboardManager.setPrimaryClip(ClipData.newPlainText(doc.title, doc.content))
                                            Toast.makeText(context, "Copied content to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Copy Content", fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { expandedDocId = null },
                                        modifier = Modifier.weight(0.6f),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        Text("Close", fontSize = 12.sp)
                                    }
                                }
                            } else {
                                Text(
                                    text = doc.content,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 16.sp
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tap to fully expand",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF005CB9)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = Color(0xFF005CB9),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getCategoryName(type: String): String {
    return when (type) {
        "cover_letter" -> "Cover Letter"
        "linkedin" -> "LinkedIn Assets"
        "interview" -> "Interview Prep Practice"
        "career" -> "Career Pathway"
        "skills_list" -> "Skills Checklist"
        "projects_list" -> "Project Showcase ideas"
        "portfolio_list" -> "Web Portfolio templates"
        "critique_letter" -> "Resume Checker Feed"
        "polish_doc" -> "Polished Grammars"
        else -> "Career Document"
    }
}

fun getCategoryColor(type: String): Color {
    return when (type) {
        "cover_letter" -> Color(0xFF3498DB)
        "linkedin" -> Color(0xFF2E86C1)
        "interview" -> Color(0xFF27AE60)
        "career" -> Color(0xFF8E44AD)
        "skills_list" -> Color(0xFFF39C12)
        "projects_list" -> Color(0xFFD35400)
        "portfolio_list" -> Color(0xFF16A085)
        else -> Color(0xFF7F8C8D)
    }
}
