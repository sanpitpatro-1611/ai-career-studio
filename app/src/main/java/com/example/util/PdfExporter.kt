package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import com.example.data.model.Resume
import com.example.data.model.Education
import com.example.data.model.Experience
import com.example.data.model.Project
import com.example.data.model.Skill
import com.example.data.model.Certificate
import com.example.data.model.Language
import com.example.data.model.Reference
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    // Postscript layout points (Letter page: 612 x 792)
    private const val PAGE_WIDTH = 612
    private const val PAGE_HEIGHT = 792
    private const val MARGIN = 40

    fun exportResumeToPdf(
        context: Context,
        resume: Resume,
        educations: List<Education>,
        experiences: List<Experience>,
        projects: List<Project>,
        skills: List<Skill>,
        certificates: List<Certificate>,
        languages: List<Language>,
        references: List<Reference>,
        fileName: String = "Resume_${System.currentTimeMillis()}.pdf"
    ): File? {
        val pdfDocument = PdfDocument()
        
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var currentPage = pdfDocument.startPage(pageInfo)
        var canvas = currentPage.canvas

        // Set up paints
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 9.5f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        val titlePaint = Paint().apply {
            color = Color.rgb(24, 76, 120) // Deep Navy/Blue Theme
            textSize = 21f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val sectionHeadingPaint = Paint().apply {
            color = Color.rgb(24, 76, 120) // M3 Primary
            textSize = 11.5f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val subHeadingPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        val grayTextPaint = Paint().apply {
            color = Color.rgb(100, 100, 100)
            textSize = 8.5f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        val italicPaint = Paint().apply {
            color = Color.rgb(80, 80, 80)
            textSize = 9f
            isAntiAlias = true
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
        }

        val linePaint = Paint().apply {
            color = Color.rgb(180, 195, 210)
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        var currentY = MARGIN + 15

        // Helpers to manage pages in mid-execution safely
        fun checkAndCreateNewPage() {
            if (currentY > PAGE_HEIGHT - MARGIN - 30) {
                pdfDocument.finishPage(currentPage)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                currentPage = pdfDocument.startPage(pageInfo)
                canvas = currentPage.canvas
                currentY = MARGIN + 20
            }
        }

        // Draw header text
        if (resume.name.isNotEmpty()) {
            titlePaint.textAlign = Paint.Align.CENTER
            canvas.drawText(resume.name.uppercase(), (PAGE_WIDTH / 2).toFloat(), currentY.toFloat(), titlePaint)
            currentY += 24
        }

        // Contact info line
        val contactItems = mutableListOf<String>()
        if (resume.email.isNotEmpty()) contactItems.add(resume.email)
        if (resume.phone.isNotEmpty()) contactItems.add(resume.phone)
        if (resume.linkedin.isNotEmpty()) contactItems.add("LinkedIn: ${resume.linkedin}")
        if (resume.github.isNotEmpty()) contactItems.add("GitHub: ${resume.github}")
        if (resume.portfolio.isNotEmpty()) contactItems.add("Portfolio: ${resume.portfolio}")

        val contactString = contactItems.joinToString("   |   ")
        if (contactString.isNotEmpty()) {
            grayTextPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(contactString, (PAGE_WIDTH / 2).toFloat(), currentY.toFloat(), grayTextPaint)
            currentY += 15
        }

        // Draw a neat top separator
        canvas.drawLine(MARGIN.toFloat(), currentY.toFloat(), (PAGE_WIDTH - MARGIN).toFloat(), currentY.toFloat(), linePaint)
        currentY += 20

        // Formats paragraphs and splits them dynamically to wrap lines
        fun drawSectionHeading(heading: String) {
            checkAndCreateNewPage()
            canvas.drawText(heading.uppercase(), MARGIN.toFloat(), currentY.toFloat(), sectionHeadingPaint)
            currentY += 4
            canvas.drawLine(MARGIN.toFloat(), currentY.toFloat(), (PAGE_WIDTH - MARGIN).toFloat(), currentY.toFloat(), linePaint)
            currentY += 14
        }

        fun drawParagraph(text: String, bullet: Boolean = false) {
            if (text.isEmpty()) return
            val prefix = if (bullet) "•  " else ""
            val marginWithPrefix = if (bullet) MARGIN + 12 else MARGIN
            val availableWidth = PAGE_WIDTH - marginWithPrefix - MARGIN

            val words = text.split(" ")
            var line = StringBuilder(prefix)

            for (word in words) {
                val nextTest = if (line.length == prefix.length) word else "${line} $word"
                val width = textPaint.measureText(nextTest)
                if (width < availableWidth) {
                    line = StringBuilder(nextTest)
                } else {
                    checkAndCreateNewPage()
                    canvas.drawText(line.toString(), marginWithPrefix.toFloat(), currentY.toFloat(), textPaint)
                    currentY += 12
                    line = StringBuilder(word)
                }
            }
            if (line.isNotEmpty()) {
                checkAndCreateNewPage()
                canvas.drawText(line.toString(), marginWithPrefix.toFloat(), currentY.toFloat(), textPaint)
                currentY += 14
            }
        }

        // OBJECTIVE / SUMMARY
        if (resume.objective.isNotEmpty()) {
            drawSectionHeading("Professional Summary")
            drawParagraph(resume.objective)
            currentY += 10
        }

        // EXPERIENCE
        if (experiences.isNotEmpty()) {
            drawSectionHeading("Professional Experience")
            for (exp in experiences) {
                checkAndCreateNewPage()
                // Company is Bold on left
                subHeadingPaint.textAlign = Paint.Align.LEFT
                canvas.drawText(exp.company.uppercase(), MARGIN.toFloat(), currentY.toFloat(), subHeadingPaint)

                // Date align to the Right
                textPaint.textAlign = Paint.Align.RIGHT
                val dateText = "${exp.startDate} - ${if (exp.isCurrent) "Present" else exp.endDate}"
                canvas.drawText(dateText, (PAGE_WIDTH - MARGIN).toFloat(), currentY.toFloat(), textPaint)
                currentY += 12

                // Role title
                checkAndCreateNewPage()
                italicPaint.textAlign = Paint.Align.LEFT
                val roleLocation = if (exp.location.isNotEmpty()) "${exp.role}   (${exp.location})" else exp.role
                canvas.drawText(roleLocation, MARGIN.toFloat(), currentY.toFloat(), italicPaint)
                currentY += 14

                // Reset drawing text align
                textPaint.textAlign = Paint.Align.LEFT

                // Role Bullet lines
                val bullets = exp.description.split("\n", "•", "-").map { it.trim() }.filter { it.isNotEmpty() }
                if (bullets.isEmpty()) {
                    drawParagraph(exp.description, bullet = true)
                } else {
                    for (b in bullets) {
                        drawParagraph(b, bullet = true)
                    }
                }
                currentY += 8
            }
            currentY += 5
        }

        // PROJECTS
        if (projects.isNotEmpty()) {
            drawSectionHeading("Key Projects")
            for (proj in projects) {
                checkAndCreateNewPage()
                // Project title on left, timeline on right
                subHeadingPaint.textAlign = Paint.Align.LEFT
                canvas.drawText(proj.title, MARGIN.toFloat(), currentY.toFloat(), subHeadingPaint)

                if (proj.timeline.isNotEmpty()) {
                    textPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText(proj.timeline, (PAGE_WIDTH - MARGIN).toFloat(), currentY.toFloat(), textPaint)
                }
                currentY += 12

                // Tech stack italic
                if (proj.technologies.isNotEmpty()) {
                    checkAndCreateNewPage()
                    italicPaint.textAlign = Paint.Align.LEFT
                    canvas.drawText("Technologies: ${proj.technologies}", MARGIN.toFloat(), currentY.toFloat(), italicPaint)
                    currentY += 13
                }

                textPaint.textAlign = Paint.Align.LEFT
                // Description bullets
                val lines = proj.description.split("\n", "•", "-").map { it.trim() }.filter { it.isNotEmpty() }
                if (lines.isEmpty()) {
                    drawParagraph(proj.description, bullet = true)
                } else {
                    for (l in lines) {
                        drawParagraph(l, bullet = true)
                    }
                }
                currentY += 8
            }
            currentY += 5
        }

        // EDUCATION
        if (educations.isNotEmpty()) {
            drawSectionHeading("Education")
            for (edu in educations) {
                checkAndCreateNewPage()
                subHeadingPaint.textAlign = Paint.Align.LEFT
                canvas.drawText(edu.institution, MARGIN.toFloat(), currentY.toFloat(), subHeadingPaint)

                // Graduation date right-aligned
                textPaint.textAlign = Paint.Align.RIGHT
                val eduDate = "${edu.startDate} - ${edu.endDate}"
                canvas.drawText(eduDate, (PAGE_WIDTH - MARGIN).toFloat(), currentY.toFloat(), textPaint)
                currentY += 12

                checkAndCreateNewPage()
                italicPaint.textAlign = Paint.Align.LEFT
                val degreeStudy = if (edu.fieldOfStudy.isNotEmpty()) "${edu.degree} in ${edu.fieldOfStudy}" else edu.degree
                canvas.drawText(degreeStudy, MARGIN.toFloat(), currentY.toFloat(), italicPaint)

                if (edu.grade.isNotEmpty()) {
                    textPaint.textAlign = Paint.Align.RIGHT
                    canvas.drawText("Grade: ${edu.grade}", (PAGE_WIDTH - MARGIN).toFloat(), currentY.toFloat(), textPaint)
                }
                currentY += 16
                textPaint.textAlign = Paint.Align.LEFT
            }
            currentY += 5
        }

        // SKILLS
        if (skills.isNotEmpty()) {
            drawSectionHeading("Core Skills")
            checkAndCreateNewPage()
            textPaint.textAlign = Paint.Align.LEFT
            val skillsLine = skills.joinToString(", ") { "${it.name} (${it.level})" }
            drawParagraph(skillsLine)
            currentY += 10
        }

        // CERTIFICATES
        if (certificates.isNotEmpty()) {
            drawSectionHeading("Certifications")
            for (cert in certificates) {
                checkAndCreateNewPage()
                subHeadingPaint.textAlign = Paint.Align.LEFT
                canvas.drawText(cert.title, MARGIN.toFloat(), currentY.toFloat(), subHeadingPaint)

                textPaint.textAlign = Paint.Align.RIGHT
                canvas.drawText(cert.date, (PAGE_WIDTH - MARGIN).toFloat(), currentY.toFloat(), textPaint)
                currentY += 12

                checkAndCreateNewPage()
                italicPaint.textAlign = Paint.Align.LEFT
                canvas.drawText("Issued by: ${cert.issuer}", MARGIN.toFloat(), currentY.toFloat(), italicPaint)
                currentY += 15
                textPaint.textAlign = Paint.Align.LEFT
            }
            currentY += 5
        }

        // LANGUAGES
        if (languages.isNotEmpty()) {
            drawSectionHeading("Languages")
            checkAndCreateNewPage()
            textPaint.textAlign = Paint.Align.LEFT
            val languagesLine = languages.joinToString(", ") { "${it.name} (${it.proficiency})" }
            drawParagraph(languagesLine)
            currentY += 10
        }

        // REFERENCES
        if (references.isNotEmpty()) {
            drawSectionHeading("Professional References")
            for (ref in references) {
                checkAndCreateNewPage()
                subHeadingPaint.textAlign = Paint.Align.LEFT
                canvas.drawText(ref.name, MARGIN.toFloat(), currentY.toFloat(), subHeadingPaint)
                currentY += 12

                checkAndCreateNewPage()
                textPaint.textAlign = Paint.Align.LEFT
                var refDetails = "${ref.relation} at ${ref.company}"
                if (ref.email.isNotEmpty()) refDetails += "  |  Email: ${ref.email}"
                if (ref.phone.isNotEmpty()) refDetails += "  |  Phone: ${ref.phone}"
                drawParagraph(refDetails)
                currentY += 8
            }
        }

        // Wrap up the PDF Document pages
        pdfDocument.finishPage(currentPage)

        // Write the PDF Document to a file
        val downloadsDir = context.getExternalFilesDir(null) ?: context.filesDir
        val file = File(downloadsDir, fileName)

        return try {
            val fileOutputStream = FileOutputStream(file)
            pdfDocument.writeTo(fileOutputStream)
            fileOutputStream.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }
}
