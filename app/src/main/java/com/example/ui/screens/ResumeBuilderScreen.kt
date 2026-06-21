package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.viewmodel.CareerStudioViewModel
import com.example.util.PdfExporter
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ResumeBuilderScreen(viewModel: CareerStudioViewModel) {
    val context = LocalContext.current
    val activeResume by viewModel.activeResumeState.collectAsState()

    if (activeResume == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Icon(Icons.Default.FolderOpen, contentDescription = "Empty", modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.secondary)
                Text("Select or create a Profile on the Dashboard first!", fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
        return
    }

    val resume = activeResume!!

    // Local states mirroring the model to prevent massive re-renders
    var name by remember(resume.id) { mutableStateOf(resume.name) }
    var phone by remember(resume.id) { mutableStateOf(resume.phone) }
    var email by remember(resume.id) { mutableStateOf(resume.email) }
    var linkedin by remember(resume.id) { mutableStateOf(resume.linkedin) }
    var github by remember(resume.id) { mutableStateOf(resume.github) }
    var portfolio by remember(resume.id) { mutableStateOf(resume.portfolio) }
    var objective by remember(resume.id) { mutableStateOf(resume.objective) }

    // Section sub-lists
    val educations = remember(resume.educationsJson) { viewModel.getEducations(resume).toMutableStateList() }
    val experiences = remember(resume.experiencesJson) { viewModel.getExperiences(resume).toMutableStateList() }
    val projects = remember(resume.projectsJson) { viewModel.getProjects(resume).toMutableStateList() }
    val skills = remember(resume.skillsJson) { viewModel.getSkills(resume).toMutableStateList() }
    val certificates = remember(resume.certificatesJson) { viewModel.getCertificates(resume).toMutableStateList() }
    val languages = remember(resume.languagesJson) { viewModel.getLanguages(resume).toMutableStateList() }
    val references = remember(resume.referencesJson) { viewModel.getReferences(resume).toMutableStateList() }

    // Accordion Expansion states
    var expandedSection by remember { mutableStateOf("contact") } // "contact", "summary", "education", "experience", "projects", "skills", "certifications", "languages", "references"

    // Dialog trigger states
    var showEducDialog by remember { mutableStateOf(false) }
    var showExpDialog by remember { mutableStateOf(false) }
    var showProjDialog by remember { mutableStateOf(false) }
    var showSkillDialog by remember { mutableStateOf(false) }
    var showCertDialog by remember { mutableStateOf(false) }
    var showLangDialog by remember { mutableStateOf(false) }
    var showRefDialog by remember { mutableStateOf(false) }

    // AI dialog helper states
    var showAiWriterDialog by remember { mutableStateOf(false) }
    var aiWriterType by remember { mutableStateOf("objective") } // "objective", "experience"
    var aiWriterSourceText by remember { mutableStateOf("") }
    val aiIsImproving by viewModel.isImprovingWording.collectAsState()
    val aiImprovedResult by viewModel.wordingImprovementResult.collectAsState()

    var activeExperienceIndexForAi by remember { mutableStateOf(-1) }

    // Auto-save logic triggers whenever main text values or sub-lists mutate
    fun saveResume() {
        viewModel.updateResumeFields(
            name = name, phone = phone, email = email, linkedin = linkedin, github = github, portfolio = portfolio,
            objective = objective, educations = educations, experiences = experiences, projects = projects,
            skills = skills, certificates = certificates, languages = languages, references = references
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("resume_builder"),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Guide", tint = MaterialTheme.colorScheme.primary)
                        Column {
                            Text("Editing Profile: ${resume.title}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("All updates are saved locally. You can export as high quality ATS templates offline.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // SECTION 1: CONTACT INFO
            item {
                AccordionSectionHeader(
                    title = "1. Personal Contact Details",
                    icon = Icons.Default.ContactPage,
                    isExpanded = expandedSection == "contact",
                    onToggle = { expandedSection = if (expandedSection == "contact") "" else "contact" }
                )
                AnimatedVisibility(visible = expandedSection == "contact") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(value = name, onValueChange = { name = it; saveResume() }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = email, onValueChange = { email = it; saveResume() }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = phone, onValueChange = { phone = it; saveResume() }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = linkedin, onValueChange = { linkedin = it; saveResume() }, label = { Text("LinkedIn Username") }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("e.g. linkedin.com/in/peter-parker") })
                        OutlinedTextField(value = github, onValueChange = { github = it; saveResume() }, label = { Text("GitHub Profile Link") }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("e.g. github.com/username") })
                        OutlinedTextField(value = portfolio, onValueChange = { portfolio = it; saveResume() }, label = { Text("Portfolio Website URL") }, modifier = Modifier.fillMaxWidth(), singleLine = true, placeholder = { Text("e.g. myportfolio.com") })
                    }
                }
            }

            // SECTION 2: OBJECTIVE / SUMMARY
            item {
                AccordionSectionHeader(
                    title = "2. Profile Objective / Summary",
                    icon = Icons.Default.Description,
                    isExpanded = expandedSection == "summary",
                    onToggle = { expandedSection = if (expandedSection == "summary") "" else "summary" }
                )
                AnimatedVisibility(visible = expandedSection == "summary") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Provide a summary of your career objective and expertise. Press the AI Co-Pilot button to clean grammar and enhance structural vocabulary.", fontSize = 12.sp, color = Color.Gray)
                        OutlinedTextField(
                            value = objective,
                            onValueChange = { objective = it; saveResume() },
                            placeholder = { Text("e.g., Forward-thinking Graduate Student with expertise in advanced software architectural principles searching for full-time opportunities...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            maxLines = 5
                        )
                        Button(
                            onClick = {
                                aiWriterType = "objective"
                                aiWriterSourceText = objective
                                showAiWriterDialog = true
                                if (objective.isNotEmpty()) viewModel.improveResumeWording(objective)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI Improve")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Enhance Objective with AI", fontSize = 12.sp)
                        }
                    }
                }
            }

            // SECTION 3: WORK EXPERIENCE
            item {
                AccordionSectionHeader(
                    title = "3. Work Experience",
                    icon = Icons.Default.Work,
                    isExpanded = expandedSection == "experience",
                    onToggle = { expandedSection = if (expandedSection == "experience") "" else "experience" }
                )
                AnimatedVisibility(visible = expandedSection == "experience") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Add professional experiences below.", fontSize = 12.sp, color = Color.Gray)
                            Button(onClick = { showExpDialog = true }, shape = RoundedCornerShape(8.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                                Text("Add Work")
                            }
                        }

                        experiences.forEachIndexed { i, exp ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${exp.role} @ ${exp.company}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        IconButton(onClick = { experiences.removeAt(i); saveResume() }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${exp.startDate} - ${if (exp.isCurrent) "Present" else exp.endDate}", fontSize = 11.sp, color = Color.Gray)
                                        if (exp.location.isNotEmpty()) Text(exp.location, fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Text(exp.description, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))

                                    Button(
                                        onClick = {
                                            aiWriterType = "experience"
                                            activeExperienceIndexForAi = i
                                            aiWriterSourceText = exp.description
                                            showAiWriterDialog = true
                                            if (exp.description.isNotEmpty()) viewModel.improveResumeWording(exp.description)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f), contentColor = MaterialTheme.colorScheme.secondary),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.align(Alignment.End),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI", modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Convert to Professional Wording", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 4: PROJECTS
            item {
                AccordionSectionHeader(
                    title = "4. Key Highlights & Projects",
                    icon = Icons.Default.Lightbulb,
                    isExpanded = expandedSection == "projects",
                    onToggle = { expandedSection = if (expandedSection == "projects") "" else "projects" }
                )
                AnimatedVisibility(visible = expandedSection == "projects") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Demonstrate core capabilities.", fontSize = 12.sp, color = Color.Gray)
                            Button(onClick = { showProjDialog = true }, shape = RoundedCornerShape(8.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                                Text("Add Project")
                            }
                        }

                        projects.forEachIndexed { i, proj ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(proj.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        IconButton(onClick = { projects.removeAt(i); saveResume() }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text("Technologies: ${proj.technologies}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary)
                                    Text(proj.description, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 5: PROFILE SKILLS
            item {
                AccordionSectionHeader(
                    title = "5. Skills & Expertise",
                    icon = Icons.Default.Psychology,
                    isExpanded = expandedSection == "skills",
                    onToggle = { expandedSection = if (expandedSection == "skills") "" else "skills" }
                )
                AnimatedVisibility(visible = expandedSection == "skills") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Add technical, design, soft skills.", fontSize = 12.sp, color = Color.Gray)
                            Button(onClick = { showSkillDialog = true }, shape = RoundedCornerShape(8.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                                Text("Add Skill")
                            }
                        }

                        // Flow style skill layout representation
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                skills.forEachIndexed { i, sk ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(sk.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("Proficiency: ${sk.level}", fontSize = 10.sp, color = Color.Gray)
                                        }
                                        IconButton(onClick = { skills.removeAt(i); saveResume() }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 6: EDUCATION
            item {
                AccordionSectionHeader(
                    title = "6. Academic History & Education",
                    icon = Icons.Default.School,
                    isExpanded = expandedSection == "education",
                    onToggle = { expandedSection = if (expandedSection == "education") "" else "education" }
                )
                AnimatedVisibility(visible = expandedSection == "education") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Academic background details.", fontSize = 12.sp, color = Color.Gray)
                            Button(onClick = { showEducDialog = true }, shape = RoundedCornerShape(8.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                                Text("Add Academic Detail")
                            }
                        }

                        educations.forEachIndexed { i, edu ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(edu.institution, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        IconButton(onClick = { educations.removeAt(i); saveResume() }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text("${edu.degree} in ${edu.fieldOfStudy}", fontSize = 12.sp)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${edu.startDate} - ${edu.endDate}", fontSize = 11.sp, color = Color.Gray)
                                        if (edu.grade.isNotEmpty()) Text("GPA/Grade: ${edu.grade}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 7: CERTIFICATIONS
            item {
                AccordionSectionHeader(
                    title = "7. Certifications",
                    icon = Icons.Default.MilitaryTech,
                    isExpanded = expandedSection == "certifications",
                    onToggle = { expandedSection = if (expandedSection == "certifications") "" else "certifications" }
                )
                AnimatedVisibility(visible = expandedSection == "certifications") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Industry standards and credentials.", fontSize = 12.sp, color = Color.Gray)
                            Button(onClick = { showCertDialog = true }, shape = RoundedCornerShape(8.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                                Text("Add Credential")
                            }
                        }

                        certificates.forEachIndexed { i, cert ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(cert.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        IconButton(onClick = { certificates.removeAt(i); saveResume() }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text("Issuer: ${cert.issuer}  |  Date: ${cert.date}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 8: LANGUAGES
            item {
                AccordionSectionHeader(
                    title = "8. Languages",
                    icon = Icons.Default.Translate,
                    isExpanded = expandedSection == "languages",
                    onToggle = { expandedSection = if (expandedSection == "languages") "" else "languages" }
                )
                AnimatedVisibility(visible = expandedSection == "languages") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Language proficiencies.", fontSize = 12.sp, color = Color.Gray)
                            Button(onClick = { showLangDialog = true }, shape = RoundedCornerShape(8.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                                Text("Add Language")
                            }
                        }

                        languages.forEachIndexed { i, lang ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${lang.name} — ${lang.proficiency}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                IconButton( onClick = { languages.removeAt(i); saveResume() } ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 9: REFERENCES
            item {
                AccordionSectionHeader(
                    title = "9. Professional References",
                    icon = Icons.Default.People,
                    isExpanded = expandedSection == "references",
                    onToggle = { expandedSection = if (expandedSection == "references") "" else "references" }
                )
                AnimatedVisibility(visible = expandedSection == "references") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Provide professional references.", fontSize = 12.sp, color = Color.Gray)
                            Button(onClick = { showRefDialog = true }, shape = RoundedCornerShape(8.dp)) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                                Text("Add Referee")
                            }
                        }

                        references.forEachIndexed { i, ref ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(ref.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        IconButton(onClick = { references.removeAt(i); saveResume() }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text("${ref.relation} at ${ref.company}", fontSize = 12.sp)
                                    Text("Relation: ${ref.relation}  |  Email: ${ref.email}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }

        // FLOATING ACTION BAR: EXPORT SELECTABLE CLASSY PDF
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primary,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        // Triggers the beautiful native PDF Exporter!
                        val pdfFile = PdfExporter.exportResumeToPdf(
                            context = context,
                            resume = resume,
                            educations = educations,
                            experiences = experiences,
                            projects = projects,
                            skills = skills,
                            certificates = certificates,
                            languages = languages,
                            references = references
                        )
                        if (pdfFile != null) {
                            Toast.makeText(context, "PDF successfully generated! Saved to files directory:\n${pdfFile.name}", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Failed to compile offline PDF, check inputs.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = "Download PDF", tint = Color.White)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Export Selectable Classy PDF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    // Modal dialogs for adding sub elements
    if (showEducDialog) {
        var school by remember { mutableStateOf("") }
        var degree by remember { mutableStateOf("") }
        var field by remember { mutableStateOf("") }
        var sDate by remember { mutableStateOf("") }
        var eDate by remember { mutableStateOf("") }
        var grade by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showEducDialog = false },
            title = { Text("Add Academic Detail") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = school, onValueChange = { school = it }, label = { Text("School/Institution") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = degree, onValueChange = { degree = it }, label = { Text("Degree (e.g. Master's)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = field, onValueChange = { field = it }, label = { Text("Field of Study") }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = sDate, onValueChange = { sDate = it }, label = { Text("Start Year") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = eDate, onValueChange = { eDate = it }, label = { Text("End Year") }, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text("GPA/Grade (Optional)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (school.isNotEmpty() && degree.isNotEmpty()) {
                        educations.add(Education(school, degree, field, sDate, eDate, grade))
                        saveResume()
                    }
                    showEducDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showEducDialog = false }) { Text("Cancel") } }
        )
    }

    if (showExpDialog) {
        var company by remember { mutableStateOf("") }
        var companyRole by remember { mutableStateOf("") }
        var startEx by remember { mutableStateOf("") }
        var endEx by remember { mutableStateOf("") }
        var locEx by remember { mutableStateOf("") }
        var isCurrentEx by remember { mutableStateOf(false) }
        var descEx by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showExpDialog = false },
            title = { Text("Add Professional Experience") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("Company Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = companyRole, onValueChange = { companyRole = it }, label = { Text("Job Title/Role") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = locEx, onValueChange = { locEx = it }, label = { Text("Location (e.g. San Jose, CA)") }, modifier = Modifier.fillMaxWidth())
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = isCurrentEx, onCheckedChange = { isCurrentEx = it })
                        Text("Current Job", fontSize = 14.sp)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = startEx, onValueChange = { startEx = it }, label = { Text("Start Date") }, modifier = Modifier.weight(1f))
                        if (!isCurrentEx) {
                            OutlinedTextField(value = endEx, onValueChange = { endEx = it }, label = { Text("End Date") }, modifier = Modifier.weight(1f))
                        }
                    }
                    OutlinedTextField(value = descEx, onValueChange = { descEx = it }, label = { Text("Responsibility Bullet Points") }, placeholder = { Text("Worked in React frontend...") }, modifier = Modifier.fillMaxWidth().height(80.dp))
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (company.isNotEmpty() && companyRole.isNotEmpty()) {
                        experiences.add(Experience(company, companyRole, locEx, startEx, endEx, descEx, isCurrentEx))
                        saveResume()
                    }
                    showExpDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showExpDialog = false }) { Text("Cancel") } }
        )
    }

    if (showProjDialog) {
        var pTitle by remember { mutableStateOf("") }
        var pTech by remember { mutableStateOf("") }
        var pTimeline by remember { mutableStateOf("") }
        var pDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showProjDialog = false },
            title = { Text("Add Project Highlight") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = pTitle, onValueChange = { pTitle = it }, label = { Text("Project Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pTech, onValueChange = { pTech = it }, label = { Text("Technologies Used") }, placeholder = { Text("Kotlin, Room DB, SQLite") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pTimeline, onValueChange = { pTimeline = it }, label = { Text("Timeline") }, placeholder = { Text("Jan 2026") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pDesc, onValueChange = { pDesc = it }, label = { Text("Full Highlights/Description") }, modifier = Modifier.fillMaxWidth().height(80.dp))
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (pTitle.isNotEmpty()) {
                        projects.add(Project(pTitle, pTech, pDesc, timeline = pTimeline))
                        saveResume()
                    }
                    showProjDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showProjDialog = false }) { Text("Cancel") } }
        )
    }

    if (showSkillDialog) {
        var sName by remember { mutableStateOf("") }
        var sLvl by remember { mutableStateOf("Intermediate") } // Beginner, Intermediate, Expert
        var dropSkillExp by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showSkillDialog = false },
            title = { Text("Add Skill Tag") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = sName, onValueChange = { sName = it }, label = { Text("Skill name (e.g. Kotlin)") }, modifier = Modifier.fillMaxWidth())
                    Box {
                        Button(onClick = { dropSkillExp = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Proficiency: $sLvl")
                        }
                        DropdownMenu(expanded = dropSkillExp, onDismissRequest = { dropSkillExp = false }) {
                            listOf("Beginner", "Intermediate", "Expert").forEach { state ->
                                DropdownMenuItem(text = { Text(state) }, onClick = { sLvl = state; dropSkillExp = false })
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (sName.isNotEmpty()) {
                        skills.add(Skill(sName, sLvl))
                        saveResume()
                    }
                    showSkillDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showSkillDialog = false }) { Text("Cancel") } }
        )
    }

    if (showCertDialog) {
        var cName by remember { mutableStateOf("") }
        var cIssuer by remember { mutableStateOf("") }
        var cDate by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCertDialog = false },
            title = { Text("Add Credential") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = cName, onValueChange = { cName = it }, label = { Text("Certification Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = cIssuer, onValueChange = { cIssuer = it }, label = { Text("Issuing Organization") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = cDate, onValueChange = { cDate = it }, label = { Text("Date of Award") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (cName.isNotEmpty() && cIssuer.isNotEmpty()) {
                        certificates.add(Certificate(cName, cIssuer, cDate))
                        saveResume()
                    }
                    showCertDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showCertDialog = false }) { Text("Cancel") } }
        )
    }

    if (showLangDialog) {
        var lName by remember { mutableStateOf("") }
        var lProf by remember { mutableStateOf("Fluent") } // Basic, Conversational, Fluent, Native
        var dropLangMenu by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showLangDialog = false },
            title = { Text("Add Language") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = lName, onValueChange = { lName = it }, label = { Text("Language (e.g. Spanish)") }, modifier = Modifier.fillMaxWidth())
                    Box {
                        Button(onClick = { dropLangMenu = true }, modifier = Modifier.fillMaxWidth()) {
                            Text("Fluency: $lProf")
                        }
                        DropdownMenu(expanded = dropLangMenu, onDismissRequest = { dropLangMenu = false }) {
                            listOf("Basic", "Conversational", "Fluent", "Native").forEach { state ->
                                DropdownMenuItem(text = { Text(state) }, onClick = { lProf = state; dropLangMenu = false })
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (lName.isNotEmpty()) {
                        languages.add(Language(lName, lProf))
                        saveResume()
                    }
                    showLangDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showLangDialog = false }) { Text("Cancel") } }
        )
    }

    if (showRefDialog) {
        var rName by remember { mutableStateOf("") }
        var rCompany by remember { mutableStateOf("") }
        var rPhone by remember { mutableStateOf("") }
        var rEmail by remember { mutableStateOf("") }
        var rRel by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showRefDialog = false },
            title = { Text("Add Professional Reference") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = rName, onValueChange = { rName = it }, label = { Text("Referee Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rCompany, onValueChange = { rCompany = it }, label = { Text("Company/Organization") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rRel, onValueChange = { rRel = it }, label = { Text("Relation/Role") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rPhone, onValueChange = { rPhone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rEmail, onValueChange = { rEmail = it }, label = { Text("Email Address") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (rName.isNotEmpty() && rCompany.isNotEmpty()) {
                        references.add(Reference(rName, rCompany, rEmail, rPhone, rRel))
                        saveResume()
                    }
                    showRefDialog = false
                }) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showRefDialog = false }) { Text("Cancel") } }
        )
    }

    // Modal dialogue for the AI WORDING enhancement process (Features 1 & 2)
    if (showAiWriterDialog) {
        Dialog(onDismissRequest = { showAiWriterDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("AI Career Wording Optimizer", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Text("Underperforming phrases decrease your ATS visibility. AI converts casual speech into punchy corporate templates instantly using high-impact active verbs.", fontSize = 12.sp, color = Color.Gray)

                    OutlinedTextField(
                        value = aiWriterSourceText,
                        onValueChange = { aiWriterSourceText = it },
                        label = { Text("Your Draft") },
                        placeholder = { Text("e.g., worked at medical shop selling medicines") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )

                    Button(
                        onClick = { viewModel.improveResumeWording(aiWriterSourceText) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !aiIsImproving && aiWriterSourceText.isNotEmpty()
                    ) {
                        if (aiIsImproving) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Consulting HR Recruiting Models...")
                        } else {
                            Text("Optimize Draft Phrasing Now")
                        }
                    }

                    if (aiImprovedResult.isNotEmpty()) {
                        Text("AI Candidate Proposal:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.04f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = aiImprovedResult,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(onClick = { showAiWriterDialog = false }, modifier = Modifier.weight(1f)) {
                                Text("Discard")
                            }
                            Button(
                                onClick = {
                                    if (aiWriterType == "objective") {
                                        objective = aiImprovedResult
                                        saveResume()
                                    } else if (aiWriterType == "experience" && activeExperienceIndexForAi in experiences.indices) {
                                        val old = experiences[activeExperienceIndexForAi]
                                        experiences[activeExperienceIndexForAi] = old.copy(description = aiImprovedResult)
                                        saveResume()
                                    }
                                    showAiWriterDialog = false
                                    Toast.makeText(context, "Applied optimization perfectly!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1.5f)
                            ) {
                                Text("Apply Proposal")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccordionSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) Color(0xFFE7F1FF) else Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isExpanded) Color(0xFF005CB9).copy(alpha = 0.3f) else Color(0xFFECEEF5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (isExpanded) Color(0xFF005CB9).copy(alpha = 0.12f) else Color(0xFFF1F3F9),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (isExpanded) Color(0xFF005CB9) else Color(0xFF001D36).copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF001D36))
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = Color(0xFF001D36).copy(alpha = 0.6f)
            )
        }
    }
}
