package com.smartnotes.services

import android.content.Context
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import kotlinx.coroutines.tasks.await

class HandwritingRecognitionService(private val context: Context) {
    private var recognizer: DigitalInkRecognizer? = null
    private var isModelDownloaded = false

    suspend fun initialize(): Boolean {
        return try {
            val modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US")
            if (modelIdentifier == null) {
                false
            } else {
                val model = DigitalInkRecognitionModel.builder(modelIdentifier).build()
                val options = DigitalInkRecognizerOptions.builder(model).build()
                recognizer = DigitalInkRecognition.getClient(options)
                isModelDownloaded = true
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun recognizeInk(ink: Ink): String? {
        return try {
            val recognizer = recognizer ?: return null
            val result = recognizer.recognize(ink).await()
            result.candidates.firstOrNull()?.text
        } catch (e: Exception) {
            null
        }
    }

    fun release() {
        recognizer?.close()
        recognizer = null
    }
}
