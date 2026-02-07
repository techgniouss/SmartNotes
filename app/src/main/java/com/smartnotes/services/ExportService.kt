package com.smartnotes.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Environment
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.smartnotes.data.entities.Note
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExportService(private val context: Context) {

    fun exportToPdf(note: Note): File? {
        return try {
            val fileName = "note_${note.id}_${System.currentTimeMillis()}.pdf"
            val file = File(getExportDirectory(), fileName)
            
            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)
            
            document.add(Paragraph(note.title).setFontSize(18f).setBold())
            document.add(Paragraph(""))
            document.add(Paragraph(formatDate(note.createdAt)).setFontSize(10f))
            document.add(Paragraph(""))
            document.add(Paragraph(note.content))
            
            if (note.tags.isNotEmpty()) {
                document.add(Paragraph(""))
                document.add(Paragraph("Tags: ${note.tags.joinToString(", ")}").setFontSize(10f))
            }
            
            if (note.mathExpressions.isNotEmpty()) {
                document.add(Paragraph(""))
                document.add(Paragraph("Math Expressions:").setBold())
                note.mathExpressions.forEach { expr ->
                    document.add(Paragraph(expr).setFontSize(12f))
                }
            }
            
            document.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportToText(note: Note): File? {
        return try {
            val fileName = "note_${note.id}_${System.currentTimeMillis()}.txt"
            val file = File(getExportDirectory(), fileName)
            
            val content = buildString {
                appendLine(note.title)
                appendLine("=".repeat(note.title.length))
                appendLine()
                appendLine("Date: ${formatDate(note.createdAt)}")
                appendLine()
                appendLine(note.content)
                
                if (note.tags.isNotEmpty()) {
                    appendLine()
                    appendLine("Tags: ${note.tags.joinToString(", ")}")
                }
                
                if (note.mathExpressions.isNotEmpty()) {
                    appendLine()
                    appendLine("Math Expressions:")
                    note.mathExpressions.forEach { expr ->
                        appendLine("  - $expr")
                    }
                }
            }
            
            file.writeText(content)
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportToImage(note: Note, width: Int = 1080, height: Int = 1920): File? {
        return try {
            val fileName = "note_${note.id}_${System.currentTimeMillis()}.png"
            val file = File(getExportDirectory(), fileName)
            
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(Color.WHITE)
            
            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 40f
                isAntiAlias = true
            }
            
            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 60f
                isFakeBoldText = true
                isAntiAlias = true
            }
            
            var y = 100f
            canvas.drawText(note.title, 50f, y, titlePaint)
            y += 100f
            
            canvas.drawText(formatDate(note.createdAt), 50f, y, paint)
            y += 80f
            
            val lines = note.content.split("\n")
            for (line in lines) {
                if (y > height - 100) break
                canvas.drawText(line, 50f, y, paint)
                y += 50f
            }
            
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getExportDirectory(): File {
        val dir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "SmartNotes"
        )
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
