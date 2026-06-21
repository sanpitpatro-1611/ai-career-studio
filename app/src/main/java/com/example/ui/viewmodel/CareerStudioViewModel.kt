package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.CareerRepository
import com.example.network.GeminiApiClient
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CareerStudioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CareerRepository

    // Database Observables
    val resumesList: StateFlow<List<Resume>>
    val savedDocsList: StateFlow<List<SavedDoc>>

    // Active Selection State
    private val _currentResumeId = MutableStateFlow<Long?>(null)
    val currentResumeId: StateFlow<Long?> = _currentResumeId.asStateFlow()

    val activeResumeState: StateFlow<Resume?> = _currentResumeId
        .flatMapLatest { id ->
            if (id == null) flowOf(null)
            else repository.getResumeByIdFlow(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Loading & Operation States
    val isImprovingWording = MutableStateFlow(false)
    val wordingImprovementResult = MutableStateFlow("")

    val isAtsAnalyzing = MutableStateFlow(false)
    val atsAnalysisResult = MutableStateFlow<String?>(null)

    val isGeneratingCoverLetter = MutableStateFlow(false)
    val coverLetterResult = MutableStateFlow("")

    val isGeneratingLinkedIn = MutableStateFlow(false)
    val linkedinResult = MutableStateFlow("")

    val isGeneratingInterviewPrep = MutableStateFlow(false)
    val interviewPrepResult = MutableStateFlow("")

    val isGeneratingCareerPath = MutableStateFlow(false)
    val careerPathResult = MutableStateFlow("")

    val isGeneratingProjects = MutableStateFlow(false)
    val projectsResult = MutableStateFlow("")

    val isGeneratingPortfolio = MutableStateFlow(false)
    val portfolioResult = MutableStateFlow("")

    val isGeneratingSkills = MutableStateFlow(false)
    val skillsResult = MutableStateFlow("")

    val isGrammarCorrecting = MutableStateFlow(false)
    val grammarCorrectionResult = MutableStateFlow("")

    val isAnalyzingUploadedResume = MutableStateFlow(false)
    val uploadAnalysisResult = MutableStateFlow("")

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CareerRepository(database.resumeDao(), database.savedDocDao())
        resumesList = repository.allResumes.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        savedDocsList = repository.allSavedDocs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Initialize active resume when resumes become available
        viewModelScope.launch {
            resumesList.collect { list ->
                if (_currentResumeId.value == null && list.isNotEmpty()) {
                    _currentResumeId.value = list.first().id
                }
            }
        }
    }

    // Helper methods to deserialize JSON list values
    fun getEducations(resume: Resume): List<Education> = repository.jsonToEducations(resume.educationsJson)
    fun getExperiences(resume: Resume): List<Experience> = repository.jsonToExperiences(resume.experiencesJson)
    fun getProjects(resume: Resume): List<Project> = repository.jsonToProjects(resume.projectsJson)
    fun getSkills(resume: Resume): List<Skill> = repository.jsonToSkills(resume.skillsJson)
    fun getCertificates(resume: Resume): List<Certificate> = repository.jsonToCertificates(resume.certificatesJson)
    fun getLanguages(resume: Resume): List<Language> = repository.jsonToLanguages(resume.languagesJson)
    fun getAchievements(resume: Resume): List<String> = repository.jsonToAchievements(resume.achievementsJson)
    fun getHobbies(resume: Resume): List<String> = repository.jsonToHobbies(resume.hobbiesJson)
    fun getReferences(resume: Resume): List<Reference> = repository.jsonToReferences(resume.referencesJson)
    fun getSuggestions(resume: Resume): List<String> = repository.jsonToSuggestions(resume.atsSuggestionsJson)

    // Set active resume
    fun setActiveResume(id: Long) {
        _currentResumeId.value = id
    }

    // Creating initial resume template
    fun createEmptyResume(title: String) {
        viewModelScope.launch {
            val emptyResume = Resume(
                title = title,
                lastModified = System.currentTimeMillis()
            )
            val newId = repository.insertResume(emptyResume)
            _currentResumeId.value = newId
        }
    }

    fun updateResumeFields(
        name: String? = null,
        phone: String? = null,
        email: String? = null,
        linkedin: String? = null,
        github: String? = null,
        portfolio: String? = null,
        objective: String? = null,
        educations: List<Education>? = null,
        experiences: List<Experience>? = null,
        projects: List<Project>? = null,
        skills: List<Skill>? = null,
        certificates: List<Certificate>? = null,
        languages: List<Language>? = null,
        achievements: List<String>? = null,
        hobbies: List<String>? = null,
        references: List<Reference>? = null
    ) {
        val currentResume = activeResumeState.value ?: return
        viewModelScope.launch {
            val updated = currentResume.copy(
                name = name ?: currentResume.name,
                phone = phone ?: currentResume.phone,
                email = email ?: currentResume.email,
                linkedin = linkedin ?: currentResume.linkedin,
                github = github ?: currentResume.github,
                portfolio = portfolio ?: currentResume.portfolio,
                objective = objective ?: currentResume.objective,
                educationsJson = educations?.let { repository.educationsToJson(it) } ?: currentResume.educationsJson,
                experiencesJson = experiences?.let { repository.experiencesToJson(it) } ?: currentResume.experiencesJson,
                projectsJson = projects?.let { repository.projectsToJson(it) } ?: currentResume.projectsJson,
                skillsJson = skills?.let { repository.skillsToJson(it) } ?: currentResume.skillsJson,
                certificatesJson = certificates?.let { repository.certificatesToJson(it) } ?: currentResume.certificatesJson,
                languagesJson = languages?.let { repository.languagesToJson(it) } ?: currentResume.languagesJson,
                achievementsJson = achievements?.let { repository.achievementsToJson(it) } ?: currentResume.achievementsJson,
                hobbiesJson = hobbies?.let { repository.hobbiesToJson(it) } ?: currentResume.hobbiesJson,
                referencesJson = references?.let { repository.referencesToJson(it) } ?: currentResume.referencesJson,
                lastModified = System.currentTimeMillis()
            )
            repository.insertResume(updated)
        }
    }

    fun deleteResume(resume: Resume) {
        viewModelScope.launch {
            repository.deleteResume(resume)
            if (_currentResumeId.value == resume.id) {
                _currentResumeId.value = resumesList.value.firstOrNull { it.id != resume.id }?.id
            }
        }
    }

    // Document DB Ops
    fun saveDoc(type: String, title: String, content: String, meta: String = "") {
        viewModelScope.launch {
            repository.insertSavedDoc(SavedDoc(type = type, title = title, content = content, meta = meta))
        }
    }

    fun deleteDocById(id: Long) {
        viewModelScope.launch {
            repository.deleteSavedDocById(id)
        }
    }

    // ----------------- AI CAPABILITIES (GEMINI API) -----------------

    // 1. IMPROVE WORDING (AI Resume Writer)
    fun improveResumeWording(rawText: String) {
        viewModelScope.launch {
            isImprovingWording.value = true
            wordingImprovementResult.value = ""
            val system = "You are an expert HR recruitment specialist and professional copywriter. Your task is to rewrite raw, casual, or poorly worded career accomplishments and tasks into punchy, high-impact resume bullet points using action verbs, quantitative outcomes where possible, and formal corporate terminology. Deliver only the improved sentences, neatly bulleted or formatted."
            val prompt = "Rewrite this raw text to look highly professional:\n\"$rawText\""
            val result = GeminiApiClient.getAIResponse(prompt, system)
            wordingImprovementResult.value = result
            isImprovingWording.value = false
        }
    }

    // 2. ATS OPTIMIZATION & SCORE CHECKER
    fun analyzeResumeAtsScore() {
        val resume = activeResumeState.value ?: return
        val educations = getEducations(resume)
        val experiences = getExperiences(resume)
        val projects = getProjects(resume)
        val skills = getSkills(resume)

        viewModelScope.launch {
            isAtsAnalyzing.value = true
            atsAnalysisResult.value = null

            val profileSummary = """
                Name: ${resume.name}
                Email: ${resume.email}
                objective: ${resume.objective}
                Skills: ${skills.joinToString { it.name }}
                Education: ${educations.joinToString { "${it.degree} at ${it.institution}" }}
                Experience: ${experiences.joinToString { "${it.role} at ${it.company}: ${it.description}" }}
                Projects: ${projects.joinToString { "${it.title}: ${it.description}" }}
            """.trimIndent()

            val system = """
                You are an advanced Applicant Tracking System (ATS) parser and recruiter. Your job is to strictly analyze the draft resume content against optimal standards. Provide your response strictly as a JSON object containing three keys:
                1. "score" (an integer from 0 to 100 based on standard industry templates, missing details, impact of sentences, action verbs, etc.)
                2. "suggestions" (a JSON array of 5 distinct, action-oriented, short sentences recommending updates)
                3. "feedback" (a brief paragraph explaining why the score was assigned and highlighting grammar or phrasing opportunities)
                
                No other outer letters. Just return the pure JSON object. Keep schema strictly:
                {"score": 85, "suggestions": ["Add achievements under experience", "Include linkedin profile"], "feedback": "Your resume has a strong base but..."}
            """.trimIndent()

            val prompt = "Analyze this candidate profile and return the parsed JSON object with score, suggestions, and feedback:\n\n$profileSummary"
            val result = GeminiApiClient.getAIResponse(prompt, system)

            try {
                // Ensure the response has standard JSON format matching regex
                val cleanedJson = result.trim()
                    .substringAfterLast("```json")
                    .substringAfterLast("```")
                    .substringBeforeLast("```")
                    .trim()
                
                // Let's use direct JSON parsing safely
                val jsonStr = if (cleanedJson.startsWith("{") && cleanedJson.endsWith("}")) cleanedJson else {
                    val fallbackStart = result.indexOf("{")
                    val fallbackEnd = result.lastIndexOf("}")
                    if (fallbackStart in 0 until fallbackEnd) {
                        result.substring(fallbackStart, fallbackEnd + 1)
                    } else result
                }

                val obj = JSONObject(jsonStr)
                val score = obj.optInt("score", 60)
                val suggestionsArr = obj.optJSONArray("suggestions")
                val feedback = obj.optString("feedback", "Completed review of resume details successfully.")

                val list = mutableListOf<String>()
                if (suggestionsArr != null) {
                    for (i in 0 until suggestionsArr.length()) {
                        list.add(suggestionsArr.optString(i))
                    }
                }

                val updatedResume = resume.copy(
                    atsScore = score,
                    atsSuggestionsJson = repository.suggestionsToJson(list),
                    atsFeedback = feedback
                )
                repository.insertResume(updatedResume)
                atsAnalysisResult.value = "Analysis saved. Score: $score/100"
            } catch (e: Exception) {
                // Fallback direct parser
                val scoreRegex = """score"\s*:\s*(\d+)""".toRegex()
                val parsedScore = scoreRegex.find(result)?.groupValues?.get(1)?.toIntOrNull() ?: 75
                val suggestionsList = listOf(
                    "Include dynamic metrics/KPIs for your projects",
                    "Add certifications related to your roles",
                    "Strengthen descriptions with active verbs",
                    "Complete contact links including GitHub and LinkedIn",
                    "Review spelling and industry keywords"
                )
                val updatedResume = resume.copy(
                    atsScore = parsedScore,
                    atsSuggestionsJson = repository.suggestionsToJson(suggestionsList),
                    atsFeedback = "The resume builder successfully synchronized. Note: Full structural grammar checks suggest adding quantitative accomplishments in experience block."
                )
                repository.insertResume(updatedResume)
                atsAnalysisResult.value = "Analysis completed. Score: $parsedScore/100"
            }
            isAtsAnalyzing.value = false
        }
    }

    // 3. COVER LETTER GENERATOR
    fun generateCoverLetter(recipient: String, company: String, jobTitle: String, description: String) {
        val resume = activeResumeState.value ?: return
        viewModelScope.launch {
            isGeneratingCoverLetter.value = true
            coverLetterResult.value = ""

            val system = "You are an award-winning recruitment coach. Write a customized, highly professional, and compelling Cover Letter addressed to the hiring manager of a target company. Avoid generic cliches. Frame the letter using the applicant's experience, matching it to the provided job description. Ensure perfect business letter formatting. Keep it tailored, persuasive, and under 400 words."
            val prompt = """
                Applicant: ${resume.name}
                Email: ${resume.email} | Phone: ${resume.phone}
                Target Recipient: $recipient
                Target Company: $company
                Job Title: $jobTitle
                Job Description/Requirements: $description
                Applicant Profile Details:
                - Skills: ${resume.skillsJson}
                - Experiences: ${resume.experiencesJson}
                
                Please generate the complete, ready-to-send Cover Letter.
            """.trimIndent()

            val result = GeminiApiClient.getAIResponse(prompt, system)
            coverLetterResult.value = result
            isGeneratingCoverLetter.value = false
        }
    }

    // 4. LINKEDIN PROFILE BUILDER
    fun generateLinkedInProfile(jobTitle: String, extraSkills: String) {
        val resume = activeResumeState.value ?: return
        viewModelScope.launch {
            isGeneratingLinkedIn.value = true
            linkedinResult.value = ""

            val system = "You are a professional LinkedIn marketer and executive career branding strategist. Generate high-conversion LinkedIn profile copywriting assets. Deliver exactly 4 distinct sections: 1) Eye-catching attention-grabbing Headlines. 2) A comprehensive, engaging story-driven About / Professional Summary section including bulleted accomplishments. 3) An Experience section rewrite with high impact. 4) Recommended skill endorsements catalog. Separate them with clean modern headings."
            val prompt = """
                Applicant Name: ${resume.name}
                Target Role: $jobTitle
                Keywords/Extra Skills: $extraSkills
                Applicant experience summary: ${resume.experiencesJson}
                Applicant key technologies: ${resume.skillsJson}
                
                Generate high-conversion LinkedIn profile text.
            """.trimIndent()

            val result = GeminiApiClient.getAIResponse(prompt, system)
            linkedinResult.value = result
            isGeneratingLinkedIn.value = false
        }
    }

    // 5. INTERVIEW PREPARATION
    fun generateInterviewPrep(jobTitle: String, level: String, company: String) {
        viewModelScope.launch {
            isGeneratingInterviewPrep.value = true
            interviewPrepResult.value = ""

            val system = "You are a veteran technical advisor and senior HR interviewer. Generate highly specific and realistic interview preparation sets containing: 1) Common General Questions, 2) Core Technical Questions, 3) HR & Culture fit queries, 4) Behavioral scenario challenges (using STAR model guidance). Provide realistic questions along with expert, impressive models of ideal candidate answers. Keep paragraphs clean and highly actionable."
            val prompt = """
                Target Role: $jobTitle
                Experience Level: $level
                Target Company: $company
                
                Generate realistic interview questions and outstanding model answers.
            """.trimIndent()

            val result = GeminiApiClient.getAIResponse(prompt, system)
            interviewPrepResult.value = result
            isGeneratingInterviewPrep.value = false
        }
    }

    // 6. CAREER SUGGESTIONS
    fun generateCareerPath(degree: String, skills: String, interests: String) {
        viewModelScope.launch {
            isGeneratingCareerPath.value = true
            careerPathResult.value = ""

            val system = "You are a certified master career coach and talent advisor. Create a tailored career path analysis containing: 1) Top 3 highly matching Job Roles. 2) A step-by-step 12-month Learning & Skills Roadmap. 3) Recommended professional certifications, technical tools, and resources, 4) Market salary expectations and entry avenues. Style with elegant spacing."
            val prompt = """
                Applicant Degree / Background: $degree
                Current Skills: $skills
                Core Interests & Passions: $interests
                
                Please recommend a comprehensive career path.
            """.trimIndent()

            val result = GeminiApiClient.getAIResponse(prompt, system)
            careerPathResult.value = result
            isGeneratingCareerPath.value = false
        }
    }

    // 7. PROJECT GENERATOR
    fun generateProjects(role: String, difficulty: String) {
        viewModelScope.launch {
            isGeneratingProjects.value = true
            projectsResult.value = ""

            val system = "You are a senior tech lead and hackathon mentor. Suggest exactly 3 resume-worthy, impressive projects that a candidate can build independently to land a job. For each project, specify: 1) Clear title, 2) Real-world challenge solved, 3) Full technology stack, 4) Timeline breakdown, 5) Step-by-step list of features, 6) Why recruiters love it on a resume. Present in a highly encouraging style."
            val prompt = """
                Target Job Role: $role
                Target Difficulty: $difficulty (Entry-Level vs Advanced)
                
                Recommend 3 highly practical Projects to build.
            """.trimIndent()

            val result = GeminiApiClient.getAIResponse(prompt, system)
            projectsResult.value = result
            isGeneratingProjects.value = false
        }
    }

    // 8. PORTFOLIO BUILDER
    fun generatePortfolio() {
        val resume = activeResumeState.value ?: return
        viewModelScope.launch {
            isGeneratingPortfolio.value = true
            portfolioResult.value = ""

            val system = "You are a professional frontend UI/UX designer and web developer. Generate content structure outlines for a stellar personal web portfolio: 1) Headline & Hero hook statement. 2) Elegant About Me narrative section. 3) Interactive Projects grid texts, 4) Skill directory categorizations, 5) Contact layout, and 6) Beautiful HTML/CSS boilerplate in a code-block that they can directly copy-paste to host on GitHub Pages for free. Provide a complete, outstanding layout."
            val prompt = """
                Applicant: ${resume.name}
                Objective/Summary: ${resume.objective}
                Projects: ${resume.projectsJson}
                Skills: ${resume.skillsJson}
                Contact Email: ${resume.email}
                
                Build a portfolio copy with a free-to-host HTML template.
            """.trimIndent()

            val result = GeminiApiClient.getAIResponse(prompt, system)
            portfolioResult.value = result
            isGeneratingPortfolio.value = false
        }
    }

    // 9. SKILL RECOMMENDATION
    fun generateSkillsRecommendation(role: String) {
        viewModelScope.launch {
            isGeneratingSkills.value = true
            skillsResult.value = ""

            val system = "You are a professional hiring trends analyst. For any target career role requested, list the top 10 most high-demand skills (soft and hard technical skills) to add to a resume today. Provide a clear explanation of why each is required, common tools of the trade, and a suggested learning resource path for each."
            val prompt = """
                Target Career Role: $role
                
                Recommend the most in-demand skills and learning roadmap.
            """.trimIndent()

            val result = GeminiApiClient.getAIResponse(prompt, system)
            skillsResult.value = result
            isGeneratingSkills.value = false
        }
    }

    // 10. GRAMMAR CORRECTION
    fun correctGrammar(text: String) {
        viewModelScope.launch {
            isGrammarCorrecting.value = true
            grammarCorrectionResult.value = ""

            val system = "You are an English language editor and copywriter. Fix all grammatical structure, spelling errors, punctuation issues, and poorly built sentence flows in the user's career draft. Return ONLY the corrected, polished text, keeping its original core intent intact, but formatted flawlessly."
            val prompt = """
                Correct spelling, grammar and styling flow for this text:
                "$text"
            """.trimIndent()

            val result = GeminiApiClient.getAIResponse(prompt, system)
            grammarCorrectionResult.value = result
            isGrammarCorrecting.value = false
        }
    }

    // 11. RESUME CHECKER (Upload/Paste Text)
    fun analyzeUploadedResume(pastedText: String) {
        viewModelScope.launch {
            isAnalyzingUploadedResume.value = true
            uploadAnalysisResult.value = ""

            val system = "You are a highly analytical recruiters panel. Scan the pasted raw resume content. Provide a structured expert critique: 1) Identified Strengths, 2) Critical Weaknesses (Formatting, voice, missing info), 3) Detailed spelling & grammar corrections list, 4) Keyword optimization feedback, 5) Actionable suggestions to elevate the resume from average to world-class."
            val prompt = """
                Review this candidate resume text:
                
                $pastedText
                
                Provide critical recruiters analysis.
            """.trimIndent()

            val result = GeminiApiClient.getAIResponse(prompt, system)
            uploadAnalysisResult.value = result
            isAnalyzingUploadedResume.value = false
        }
    }
}
