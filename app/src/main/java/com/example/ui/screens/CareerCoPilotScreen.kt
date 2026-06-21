package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.CareerStudioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerCoPilotScreen(viewModel: CareerStudioViewModel) {
    val context = LocalContext.current
    val clipboardManager = remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    var selectedTool by remember { mutableStateOf("") } // "", "letter", "linkedin", "interview", "roadmap", "skills", "projects", "portfolio", "critique", "grammar"

    if (selectedTool.isNotEmpty()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (selectedTool) {
                                "letter" -> "Cover Letter Writer"
                                "linkedin" -> "LinkedIn Profile Builder"
                                "interview" -> "Interview Prep Practice"
                                "roadmap" -> "Career Pathway Roadmap"
                                "skills" -> "Trending Skills Optimizer"
                                "projects" -> "Resume Project Suggestions"
                                "portfolio" -> "Portfolio Website Builder"
                                "critique" -> "Resume Text Checker"
                                "grammar" -> "Grammar/Spelling Polisher"
                                else -> "AI Co-Pilot"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { selectedTool = "" }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (selectedTool) {
                    "letter" -> CoverLetterTool(viewModel, clipboardManager, context) { selectedTool = "" }
                    "linkedin" -> LinkedInTool(viewModel, clipboardManager, context) { selectedTool = "" }
                    "interview" -> InterviewPrepTool(viewModel, clipboardManager, context) { selectedTool = "" }
                    "roadmap" -> CareerRoadmapTool(viewModel, clipboardManager, context) { selectedTool = "" }
                    "skills" -> SkillsTrendingTool(viewModel, clipboardManager, context) { selectedTool = "" }
                    "projects" -> ResumeProjectsTool(viewModel, clipboardManager, context) { selectedTool = "" }
                    "portfolio" -> PortfolioBuilderTool(viewModel, clipboardManager, context) { selectedTool = "" }
                    "critique" -> ResumeCheckerTool(viewModel, clipboardManager, context) { selectedTool = "" }
                    "grammar" -> GrammarPolisherTool(viewModel, clipboardManager, context) { selectedTool = "" }
                }
            }
        }
        return
    }

    // Master list of available AI tools
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("copilot_main"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text("Career AI Co-Pilot", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF001D36))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Generate recruiter-grade assets, plan roadmaps, and optimize existing resources for free using professional HR expert models.", fontSize = 12.sp, color = Color(0xFF64748B))
        }

        // Grid List of Tools
        ToolGridItem(
            title = "Cover Letter Generator",
            description = "Write personalized, persuasive cover letters tailored to target job requirements.",
            icon = Icons.Default.MailOutline,
            color = Color(0xFF005CB9)
        ) { selectedTool = "letter" }

        ToolGridItem(
            title = "LinkedIn Profile Builder",
            description = "Generate strategic professional summaries, role copies, and viral headlines.",
            icon = Icons.Default.GroupAdd,
            color = Color(0xFF004289)
        ) { selectedTool = "linkedin" }

        ToolGridItem(
            title = "Interview Preparation",
            description = "Create realistic role questions complete with HR, behavioral, and technical model responses.",
            icon = Icons.Default.Quiz,
            color = Color(0xFF00B93C)
        ) { selectedTool = "interview" }

        ToolGridItem(
            title = "Career Suggestion & Roadmap",
            description = "Gain a detailed 12-month timeline, certifications, and expected salaries matching your degree.",
            icon = Icons.Default.Timeline,
            color = Color(0xFF5C00B9)
        ) { selectedTool = "roadmap" }

        ToolGridItem(
            title = "Resume Project Generator",
            description = "Struggling with experience? Get hackathon-grade project definitions packed with details.",
            icon = Icons.Default.SettingsSuggest,
            color = Color(0xFFB95C00)
        ) { selectedTool = "projects" }

        ToolGridItem(
            title = "Portfolio Boilerplate builder",
            description = "Generate about sections, project pitches, and complete code to host free on GitHub Pages.",
            icon = Icons.Default.Web,
            color = Color(0xFF16A085)
        ) { selectedTool = "portfolio" }

        ToolGridItem(
            title = "Resume Checker & Critique",
            description = "Upload or paste any existing text resume. AI checks weak sentences, keywords, missing details.",
            icon = Icons.Default.FactCheck,
            color = Color(0xFFC0392B)
        ) { selectedTool = "critique" }

        ToolGridItem(
            title = "Grammar & Spelling Corrector",
            description = "Polish rough paragraphs, improve grammatical flows, and translate to professional drafts.",
            icon = Icons.Default.Spellcheck,
            color = Color(0xFF2C3E50)
        ) { selectedTool = "grammar" }

        ToolGridItem(
            title = "Trending Skills Recommender",
            description = "Enter any target career role. Get the top 10 demanding tools of the trade and resource paths.",
            icon = Icons.Default.Star,
            color = Color(0xFFF39C12)
        ) { selectedTool = "skills" }
    }
}

@Composable
fun ToolGridItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECEEF5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(24.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF001D36))
                Spacer(modifier = Modifier.height(2.dp))
                Text(description, fontSize = 11.sp, color = Color(0xFF64748B), lineHeight = 15.sp)
            }

            Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = Color(0xFF001D36).copy(alpha = 0.3f))
        }
    }
}

// 1. COVER LETTER SUB-TOOL
@Composable
fun CoverLetterTool(
    viewModel: CareerStudioViewModel,
    clipboard: ClipboardManager,
    context: Context,
    onBack: () -> Unit
) {
    var recName by remember { mutableStateOf("") }
    var compName by remember { mutableStateOf("") }
    var jTitle by remember { mutableStateOf("") }
    var jDesc by remember { mutableStateOf("") }

    val isGenerating by viewModel.isGeneratingCoverLetter.collectAsState()
    val letterResult by viewModel.coverLetterResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Compose Cover Letter", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        OutlinedTextField(value = recName, onValueChange = { recName = it }, label = { Text("Hiring Manager/Recipient Name") }, placeholder = { Text("e.g. Hiring Manager, Technical Recruiter, John Smith") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = compName, onValueChange = { compName = it }, label = { Text("Target Company") }, placeholder = { Text("e.g. Acme Corporation") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = jTitle, onValueChange = { jTitle = it }, label = { Text("Job Position Title") }, placeholder = { Text("e.g. Junior Web Architect") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = jDesc, onValueChange = { jDesc = it }, label = { Text("Job Description / Bullet Requirements") }, modifier = Modifier.fillMaxWidth().height(90.dp))

        Button(
            onClick = { viewModel.generateCoverLetter(recName, compName, jTitle, jDesc) },
            enabled = !isGenerating && compName.isNotEmpty() && jTitle.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyzing active Resume & generating letter...")
            } else {
                Text("Write Customized Cover Letter")
            }
        }

        AIResultArea(
            resultText = letterResult,
            onCopy = {
                clipboard.setPrimaryClip(ClipData.newPlainText("Cover Letter", letterResult))
                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            onSave = {
                viewModel.saveDoc("cover_letter", "Cover Letter: $compName ($jTitle)", letterResult)
                Toast.makeText(context, "Saved to Offline History!", Toast.LENGTH_SHORT).show()
                onBack()
            }
        )
    }
}

// 2. LINKEDIN SUB-TOOL
@Composable
fun LinkedInTool(
    viewModel: CareerStudioViewModel,
    clipboard: ClipboardManager,
    context: Context,
    onBack: () -> Unit
) {
    var jTitle by remember { mutableStateOf("") }
    var extraSkills by remember { mutableStateOf("") }

    val isGenerating by viewModel.isGeneratingLinkedIn.collectAsState()
    val result by viewModel.linkedinResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Build LinkedIn Profile Assets", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        OutlinedTextField(value = jTitle, onValueChange = { jTitle = it }, label = { Text("Target Role/Focus") }, placeholder = { Text("e.g. Cloud Engineer, Data Analyst") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = extraSkills, onValueChange = { extraSkills = it }, label = { Text("Focus Keywords / Buzzwords") }, placeholder = { Text("e.g. AWS, Docker, Kubernetes, Remote Work") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = { viewModel.generateLinkedInProfile(jTitle, extraSkills) },
            enabled = !isGenerating && jTitle.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Assembling Profile Bio assets...")
            } else {
                Text("Generate Profile Text Copy")
            }
        }

        AIResultArea(
            resultText = result,
            onCopy = {
                clipboard.setPrimaryClip(ClipData.newPlainText("LinkedIn Profile", result))
                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            onSave = {
                viewModel.saveDoc("linkedin", "LinkedIn: $jTitle Profile", result)
                Toast.makeText(context, "Saved to Offline History!", Toast.LENGTH_SHORT).show()
                onBack()
            }
        )
    }
}

// 3. INTERVIEW PREP SUB-TOOL
@Composable
fun InterviewPrepTool(
    viewModel: CareerStudioViewModel,
    clipboard: ClipboardManager,
    context: Context,
    onBack: () -> Unit
) {
    var jTitle by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var compName by remember { mutableStateOf("") }

    val isGenerating by viewModel.isGeneratingInterviewPrep.collectAsState()
    val result by viewModel.interviewPrepResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Generate Practice Interview Q&As", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        OutlinedTextField(value = jTitle, onValueChange = { jTitle = it }, label = { Text("Target Position/Title") }, placeholder = { Text("e.g. Senior Android Engineer") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = level, onValueChange = { level = it }, label = { Text("Experience Level") }, placeholder = { Text("e.g. Entry-Level, 5 Years, Internship") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = compName, onValueChange = { compName = it }, label = { Text("Hiring Company (Optional)") }, placeholder = { Text("e.g. Google, Chase Bank") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

        Button(
            onClick = { viewModel.generateInterviewPrep(jTitle, level, compName) },
            enabled = !isGenerating && jTitle.isNotEmpty() && level.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generating custom Q&A panels...")
            } else {
                Text("Generate Practice Q&A Set")
            }
        }

        AIResultArea(
            resultText = result,
            onCopy = {
                clipboard.setPrimaryClip(ClipData.newPlainText("Interview Prep", result))
                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            onSave = {
                viewModel.saveDoc("interview", "Interview Prep: $jTitle ($level)", result)
                Toast.makeText(context, "Saved to Offline History!", Toast.LENGTH_SHORT).show()
                onBack()
            }
        )
    }
}

// 4. CAREER ROADMAP SUB-TOOL
@Composable
fun CareerRoadmapTool(
    viewModel: CareerStudioViewModel,
    clipboard: ClipboardManager,
    context: Context,
    onBack: () -> Unit
) {
    var degree by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var interests by remember { mutableStateOf("") }

    val isGenerating by viewModel.isGeneratingCareerPath.collectAsState()
    val result by viewModel.careerPathResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Generate 12-Month Learning Guide", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        OutlinedTextField(value = degree, onValueChange = { degree = it }, label = { Text("Academic Degree / Field") }, placeholder = { Text("e.g. Bachelor of Business Administration, BTech CS") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = skills, onValueChange = { skills = it }, label = { Text("Your Current Skills") }, placeholder = { Text("e.g. basic Python, public speaking") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = interests, onValueChange = { interests = it }, label = { Text("Your Core Passions / Interests") }, placeholder = { Text("e.g. cloud security, financial modeling") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = { viewModel.generateCareerPath(degree, skills, interests) },
            enabled = !isGenerating && degree.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Formulating career trajectory graphs...")
            } else {
                Text("Design Career Learning Path")
            }
        }

        AIResultArea(
            resultText = result,
            onCopy = {
                clipboard.setPrimaryClip(ClipData.newPlainText("Career Roadmap", result))
                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            onSave = {
                viewModel.saveDoc("career", "Career Roadmap: $degree", result)
                Toast.makeText(context, "Saved to Offline History!", Toast.LENGTH_SHORT).show()
                onBack()
            }
        )
    }
}

// 5. SKILLS TRENDING SUB-TOOL
@Composable
fun SkillsTrendingTool(
    viewModel: CareerStudioViewModel,
    clipboard: ClipboardManager,
    context: Context,
    onBack: () -> Unit
) {
    var role by remember { mutableStateOf("") }

    val isGenerating by viewModel.isGeneratingSkills.collectAsState()
    val result by viewModel.skillsResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Demanding Skills Trend Board", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Target Career Role") }, placeholder = { Text("e.g. DevOps Architect, UX Researcher") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

        Button(
            onClick = { viewModel.generateSkillsRecommendation(role) },
            enabled = !isGenerating && role.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tracing modern industry hiring trends...")
            } else {
                Text("Analyze Current Critical Skills")
            }
        }

        AIResultArea(
            resultText = result,
            onCopy = {
                clipboard.setPrimaryClip(ClipData.newPlainText("Skills Recommendations", result))
                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            onSave = {
                viewModel.saveDoc("skills_list", "Required Skills: $role", result)
                Toast.makeText(context, "Saved to Offline History!", Toast.LENGTH_SHORT).show()
                onBack()
            }
        )
    }
}

// 6. RESUME PROJECTS HIGHLIGHT
@Composable
fun ResumeProjectsTool(
    viewModel: CareerStudioViewModel,
    clipboard: ClipboardManager,
    context: Context,
    onBack: () -> Unit
) {
    var role by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Entry-Level Portfolio Addition") }
    var difficultyMenuExp by remember { mutableStateOf(false) }

    val isGenerating by viewModel.isGeneratingProjects.collectAsState()
    val result by viewModel.projectsResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Suggest Resume-Worthy Projects", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Target Work Role") }, placeholder = { Text("e.g. Mobile Developer, Financial Analyst") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

        Box {
            Button(onClick = { difficultyMenuExp = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Difficulty Target: $difficulty")
            }
            DropdownMenu(expanded = difficultyMenuExp, onDismissRequest = { difficultyMenuExp = false }) {
                listOf("Entry-Level Portfolio Addition", "Advanced Professional Showcase Project").forEach { d ->
                    DropdownMenuItem(text = { Text(d) }, onClick = { difficulty = d; difficultyMenuExp = false })
                }
            }
        }

        Button(
            onClick = { viewModel.generateProjects(role, difficulty) },
            enabled = !isGenerating && role.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generating hackathon templates...")
            } else {
                Text("Recommend 3 Practical Projects")
            }
        }

        AIResultArea(
            resultText = result,
            onCopy = {
                clipboard.setPrimaryClip(ClipData.newPlainText("Project Ideas", result))
                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            onSave = {
                viewModel.saveDoc("projects_list", "Project Ideas: $role ($difficulty)", result)
                Toast.makeText(context, "Saved to Offline History!", Toast.LENGTH_SHORT).show()
                onBack()
            }
        )
    }
}

// 7. PORTFOLIO WEBPAGE BUILDER
@Composable
fun PortfolioBuilderTool(
    viewModel: CareerStudioViewModel,
    clipboard: ClipboardManager,
    context: Context,
    onBack: () -> Unit
) {
    val isGenerating by viewModel.isGeneratingPortfolio.collectAsState()
    val result by viewModel.portfolioResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Personal Web Portfolio Builder", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("Produces headlines, text grids, and pre-formatted HTML / CSS web template matching your resume details to host easily for free.", fontSize = 12.sp, color = Color.Gray)

        Button(
            onClick = { viewModel.generatePortfolio() },
            enabled = !isGenerating,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Designing layout & web structures...")
            } else {
                Text("Write Copy & Generate Boilerplate Web Page")
            }
        }

        AIResultArea(
            resultText = result,
            onCopy = {
                clipboard.setPrimaryClip(ClipData.newPlainText("Portfolio Boilerplate", result))
                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            onSave = {
                viewModel.saveDoc("portfolio_list", "Web Portfolio Template", result)
                Toast.makeText(context, "Saved to Offline History!", Toast.LENGTH_SHORT).show()
                onBack()
            }
        )
    }
}

// 8. RESUME CHECKER & CRITIQUE
@Composable
fun ResumeCheckerTool(
    viewModel: CareerStudioViewModel,
    clipboard: ClipboardManager,
    context: Context,
    onBack: () -> Unit
) {
    var rawText by remember { mutableStateOf("") }

    val isAnalyzing by viewModel.isAnalyzingUploadedResume.collectAsState()
    val result by viewModel.uploadAnalysisResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Import or Paste Current Resume", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        OutlinedTextField(
            value = rawText,
            onValueChange = { rawText = it },
            placeholder = { Text("Paste your full resume text here to analyze for errors, grammar, spelling, and missing ATS benchmarks...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Button(
            onClick = { viewModel.analyzeUploadedResume(rawText) },
            enabled = !isAnalyzing && rawText.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Recruiters scanning draft details...")
            } else {
                Text("Critique Paste Material")
            }
        }

        AIResultArea(
            resultText = result,
            onCopy = {
                clipboard.setPrimaryClip(ClipData.newPlainText("Resume Critique", result))
                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            onSave = {
                viewModel.saveDoc("critique_letter", "Resume Evaluation Feedback", result)
                Toast.makeText(context, "Saved to Offline History!", Toast.LENGTH_SHORT).show()
                onBack()
            }
        )
    }
}

// 9. GRAMMAR POLISHER SUB-TOOL
@Composable
fun GrammarPolisherTool(
    viewModel: CareerStudioViewModel,
    clipboard: ClipboardManager,
    context: Context,
    onBack: () -> Unit
) {
    var rawText by remember { mutableStateOf("") }

    val isGenerating by viewModel.isGrammarCorrecting.collectAsState()
    val result by viewModel.grammarCorrectionResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Proofread & Elevate Phrasing", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        OutlinedTextField(
            value = rawText,
            onValueChange = { rawText = it },
            placeholder = { Text("Paste any rough sentence, paragraph, letter, or bio to fix formatting, spelling, grammar, and elevate structure seamlessly...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        )

        Button(
            onClick = { viewModel.correctGrammar(rawText) },
            enabled = !isGenerating && rawText.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Polishing vocabulary syntax...")
            } else {
                Text("Fix Grammar & Spelling")
            }
        }

        AIResultArea(
            resultText = result,
            onCopy = {
                clipboard.setPrimaryClip(ClipData.newPlainText("Polished Draft", result))
                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
            },
            onSave = {
                viewModel.saveDoc("polish_doc", "Polished Draft Text", result)
                Toast.makeText(context, "Saved to Offline History!", Toast.LENGTH_SHORT).show()
                onBack()
            }
        )
    }
}

// Custom UI Block for the result outputs
@Composable
fun AIResultArea(
    resultText: String,
    onCopy: () -> Unit,
    onSave: () -> Unit
) {
    AnimatedVisibility(
        visible = resultText.isNotEmpty(),
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("AI Co-Pilot Suggestion:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = resultText,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCopy,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy", fontSize = 13.sp)
                        }

                        Button(
                            onClick = onSave,
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = "Save Offline", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Offline", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
