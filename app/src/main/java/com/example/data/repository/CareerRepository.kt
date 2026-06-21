package com.example.data.repository

import com.example.data.database.ResumeDao
import com.example.data.database.SavedDocDao
import com.example.data.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class CareerRepository(
    private val resumeDao: ResumeDao,
    private val savedDocDao: SavedDocDao
) {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    private val educationListAdapter = moshi.adapter<List<Education>>(Types.newParameterizedType(List::class.java, Education::class.java))
    private val experienceListAdapter = moshi.adapter<List<Experience>>(Types.newParameterizedType(List::class.java, Experience::class.java))
    private val projectListAdapter = moshi.adapter<List<Project>>(Types.newParameterizedType(List::class.java, Project::class.java))
    private val skillListAdapter = moshi.adapter<List<Skill>>(Types.newParameterizedType(List::class.java, Skill::class.java))
    private val certificateListAdapter = moshi.adapter<List<Certificate>>(Types.newParameterizedType(List::class.java, Certificate::class.java))
    private val languageListAdapter = moshi.adapter<List<Language>>(Types.newParameterizedType(List::class.java, Language::class.java))
    private val referenceListAdapter = moshi.adapter<List<Reference>>(Types.newParameterizedType(List::class.java, Reference::class.java))
    private val stringListAdapter = moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))

    fun educationsToJson(list: List<Education>): String = educationListAdapter.toJson(list)
    fun jsonToEducations(json: String): List<Education> = try { educationListAdapter.fromJson(json) ?: emptyList() } catch(e: Exception) { emptyList() }

    fun experiencesToJson(list: List<Experience>): String = experienceListAdapter.toJson(list)
    fun jsonToExperiences(json: String): List<Experience> = try { experienceListAdapter.fromJson(json) ?: emptyList() } catch(e: Exception) { emptyList() }

    fun projectsToJson(list: List<Project>): String = projectListAdapter.toJson(list)
    fun jsonToProjects(json: String): List<Project> = try { projectListAdapter.fromJson(json) ?: emptyList() } catch(e: Exception) { emptyList() }

    fun skillsToJson(list: List<Skill>): String = skillListAdapter.toJson(list)
    fun jsonToSkills(json: String): List<Skill> = try { skillListAdapter.fromJson(json) ?: emptyList() } catch(e: Exception) { emptyList() }

    fun certificatesToJson(list: List<Certificate>): String = certificateListAdapter.toJson(list)
    fun jsonToCertificates(json: String): List<Certificate> = try { certificateListAdapter.fromJson(json) ?: emptyList() } catch(e: Exception) { emptyList() }

    fun languagesToJson(list: List<Language>): String = languageListAdapter.toJson(list)
    fun jsonToLanguages(json: String): List<Language> = try { languageListAdapter.fromJson(json) ?: emptyList() } catch(e: Exception) { emptyList() }

    fun achievementsToJson(list: List<String>): String = stringListAdapter.toJson(list)
    fun jsonToAchievements(json: String): List<String> = try { stringListAdapter.fromJson(json) ?: emptyList() } catch(e: Exception) { emptyList() }

    fun hobbiesToJson(list: List<String>): String = stringListAdapter.toJson(list)
    fun jsonToHobbies(json: String): List<String> = try { stringListAdapter.fromJson(json) ?: emptyList() } catch(e: Exception) { emptyList() }

    fun referencesToJson(list: List<Reference>): String = referenceListAdapter.toJson(list)
    fun jsonToReferences(json: String): List<Reference> = try { referenceListAdapter.fromJson(json) ?: emptyList() } catch(e: Exception) { emptyList() }

    fun suggestionsToJson(list: List<String>): String = stringListAdapter.toJson(list)
    fun jsonToSuggestions(json: String): List<String> = try { stringListAdapter.fromJson(json) ?: emptyList() } catch(e: Exception) { emptyList() }

    // Resume Database Operations
    val allResumes = resumeDao.getAllResumes()
    suspend fun getResumeById(id: Long) = resumeDao.getResumeById(id)
    fun getResumeByIdFlow(id: Long) = resumeDao.getResumeByIdFlow(id)
    suspend fun insertResume(resume: Resume): Long = resumeDao.insertResume(resume)
    suspend fun deleteResume(resume: Resume) = resumeDao.deleteResume(resume)

    // Saved Documents Database Operations
    val allSavedDocs = savedDocDao.getAllSavedDocs()
    fun getSavedDocsByType(type: String) = savedDocDao.getSavedDocsByType(type)
    suspend fun insertSavedDoc(doc: SavedDoc): Long = savedDocDao.insertSavedDoc(doc)
    suspend fun deleteSavedDoc(doc: SavedDoc) = savedDocDao.deleteSavedDoc(doc)
    suspend fun deleteSavedDocById(id: Long) = savedDocDao.deleteSavedDocById(id)
}
