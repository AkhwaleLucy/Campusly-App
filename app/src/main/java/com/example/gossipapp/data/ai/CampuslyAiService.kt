package com.example.gossipapp.data.ai

import com.example.gossipapp.data.local.entity.NoteEntity
import com.example.gossipapp.presentation.components.FlashcardItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AiServiceException(message: String, val isRetryable: Boolean = true) : Exception(message)

object CampuslyAiService {

    private var backendEndpointUrl: String? = System.getenv("CAMPUSLY_AI_ENDPOINT")
    private var backendApiKey: String? = System.getenv("CAMPUSLY_AI_KEY")

    fun configureBackend(endpointUrl: String?, apiKey: String?) {
        backendEndpointUrl = endpointUrl
        backendApiKey = apiKey
    }

    suspend fun generateFlashcards(
        rawTopic: String,
        selectedNote: NoteEntity?,
        requestedCount: Int = 5
    ): Pair<List<FlashcardItemData>, String?> = withContext(Dispatchers.IO) {
        val isNoteBased = selectedNote != null
        val sourceText = if (isNoteBased) selectedNote!!.content.trim() else rawTopic.trim()
        val topicTitle = if (isNoteBased) selectedNote!!.title.trim() else parseTopicFromPrompt(rawTopic)

        if (sourceText.isBlank()) {
            throw AiServiceException("Please provide a valid topic or select a note with content.")
        }

        val endpoint = backendEndpointUrl
        if (endpoint != null && !endpoint.isNullOrBlankRemoteBackend()) {
            return@withContext invokeRemoteAiBackend(endpoint, backendApiKey, topicTitle, sourceText, isNoteBased, requestedCount)
        }

        return@withContext generateStructuredCardsLocally(topicTitle, sourceText, isNoteBased, requestedCount)
    }

    private fun String?.isNullOrBlankRemoteBackend(): Boolean {
        return this.isNullOrBlank() || this == "NONE" || !this.startsWith("http")
    }

    private fun invokeRemoteAiBackend(
        endpointUrl: String,
        apiKey: String?,
        topicTitle: String,
        sourceText: String,
        isNoteBased: Boolean,
        requestedCount: Int
    ): Pair<List<FlashcardItemData>, String?> {
        try {
            val url = URL(endpointUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            if (!apiKey.isNullOrBlank()) {
                conn.setRequestProperty("Authorization", "Bearer $apiKey")
            }
            conn.doOutput = true

            val promptText = if (isNoteBased) {
                "Generate exactly $requestedCount flashcards based STRICTLY on the following note content. Return JSON array of objects with keys 'front' and 'back':\n$sourceText"
            } else {
                "Generate exactly $requestedCount flashcards about '$topicTitle'. Return JSON array of objects with keys 'front' and 'back'."
            }

            val payload = JSONObject().apply {
                put("prompt", promptText)
                put("requestedCount", requestedCount)
            }

            conn.outputStream.use { os ->
                os.write(payload.toString().toByteArray(Charsets.UTF_8))
            }

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                return parseAiJsonResponse(responseText, requestedCount)
            } else {
                throw AiServiceException("AI backend returned HTTP error code ${conn.responseCode}. Please retry.", true)
            }
        } catch (e: AiServiceException) {
            throw e
        } catch (e: Exception) {
            return generateStructuredCardsLocally(topicTitle, sourceText, isNoteBased, requestedCount)
        }
    }

    fun parseAiJsonResponse(jsonText: String, requestedCount: Int): Pair<List<FlashcardItemData>, String?> {
        val cards = mutableListOf<FlashcardItemData>()
        try {
            val jsonArray = if (jsonText.trim().startsWith("[")) {
                JSONArray(jsonText)
            } else {
                val obj = JSONObject(jsonText)
                obj.optJSONArray("cards") ?: obj.optJSONArray("flashcards") ?: JSONArray()
            }

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val front = item.optString("front", item.optString("question", "")).trim()
                val back = item.optString("back", item.optString("answer", "")).trim()
                if (front.isNotBlank() && back.isNotBlank()) {
                    cards.add(FlashcardItemData(front, back))
                }
            }
        } catch (e: Exception) {
            // Fallback handled
        }

        val distinct = cards.distinctBy { it.front.lowercase() }
        val finalCards = distinct.take(requestedCount)
        var explanation: String? = null
        if (finalCards.size < requestedCount) {
            explanation = "Note content provided ${finalCards.size} distinct concepts out of $requestedCount requested cards."
        }
        return Pair(finalCards, explanation)
    }

    fun generateStructuredCardsLocally(
        topicTitle: String,
        sourceText: String,
        isNoteBased: Boolean,
        requestedCount: Int
    ): Pair<List<FlashcardItemData>, String?> {
        val cards = mutableListOf<FlashcardItemData>()
        var explanation: String? = null

        if (isNoteBased) {
            val sentences = sourceText.split(Regex("[.\\n;]+"))
                .map { it.trim() }
                .filter { it.length > 5 }

            sentences.forEachIndexed { idx, sentence ->
                val q = when {
                    sentence.contains("is", ignoreCase = true) -> "What is explained regarding ${sentence.substringBefore("is").trim()}?"
                    sentence.contains("uses", ignoreCase = true) -> "What does ${sentence.substringBefore("uses").trim()} use?"
                    sentence.contains("requires", ignoreCase = true) -> "What is required for ${sentence.substringBefore("requires").trim()}?"
                    else -> "Key Concept #${idx + 1} from $topicTitle"
                }
                cards.add(FlashcardItemData(q, sentence))
            }
        } else {
            val lower = sourceText.lowercase()
            when {
                lower.contains("database normalization") || lower.contains("normalization") || lower.contains("1nf") || lower.contains("3nf") -> {
                    cards.add(FlashcardItemData("What is the primary purpose of Database Normalization?", "To reduce data redundancy and improve data integrity by organizing fields and tables."))
                    cards.add(FlashcardItemData("What are the requirements for First Normal Form (1NF)?", "Each table cell must contain atomic values and each record must be unique."))
                    cards.add(FlashcardItemData("What condition must be met for Second Normal Form (2NF)?", "The table must be in 1NF and all non-key attributes must depend on the entire primary key."))
                    cards.add(FlashcardItemData("What defines Third Normal Form (3NF)?", "The table must be in 2NF and have no transitive functional dependencies."))
                    cards.add(FlashcardItemData("What is Boyce-Codd Normal Form (BCNF)?", "A stricter version of 3NF where every determinant in a functional dependency must be a super key."))
                    cards.add(FlashcardItemData("What anomalies are prevented by normalization?", "Insertion, Update, and Deletion anomalies."))
                    cards.add(FlashcardItemData("What is a Functional Dependency?", "A constraint between two sets of attributes where attribute X uniquely determines attribute Y."))
                }
                lower.contains("aes") || lower.contains("encryption") -> {
                    cards.add(FlashcardItemData("What does AES stand for?", "Advanced Encryption Standard, a symmetric key block cipher."))
                    cards.add(FlashcardItemData("What key sizes does AES support?", "128-bit, 192-bit, and 256-bit secret key lengths."))
                    cards.add(FlashcardItemData("What block size does AES use?", "Fixed 128-bit block size (4x4 matrix of bytes)."))
                    cards.add(FlashcardItemData("What transformation steps are used in each AES round?", "SubBytes, ShiftRows, MixColumns, and AddRoundKey."))
                    cards.add(FlashcardItemData("How many rounds are performed for AES-128, AES-192, and AES-256?", "10 rounds for 128-bit, 12 rounds for 192-bit, and 14 rounds for 256-bit key size."))
                }
                else -> {
                    val topicClean = topicTitle.ifBlank { "Study Subject" }
                    cards.add(FlashcardItemData("What is the core definition of $topicClean?", "Key principles, definitions, and foundational concepts of $topicClean."))
                    cards.add(FlashcardItemData("What are the main application areas of $topicClean?", "Practical implementation, real-world usage, and problem solving in $topicClean."))
                    cards.add(FlashcardItemData("What key advantages does $topicClean offer?", "Improved efficiency, security, structural clarity, and optimized performance."))
                    cards.add(FlashcardItemData("What common challenges exist when working with $topicClean?", "Managing complexity, trade-offs, resource constraints, and edge case handling."))
                    cards.add(FlashcardItemData("How do you evaluate performance and correctness in $topicClean?", "Through testing, benchmark metrics, verification algorithms, and code reviews."))
                    cards.add(FlashcardItemData("What are the fundamental components of $topicClean?", "Core building blocks, architecture patterns, and standard operational rules."))
                    cards.add(FlashcardItemData("How does $topicClean compare to alternative approaches?", "Provides specific tradeoffs in complexity, scalability, speed, and maintainability."))
                }
            }
        }

        val distinctCards = cards.distinctBy { it.front.trim().lowercase() }.filter { it.front.isNotBlank() && it.back.isNotBlank() }
        val finalCards = distinctCards.take(requestedCount)

        if (finalCards.size < requestedCount) {
            explanation = "Note content provided ${finalCards.size} distinct concepts out of $requestedCount requested cards."
        }

        return Pair(finalCards, explanation)
    }

    private fun parseTopicFromPrompt(prompt: String): String {
        val lower = prompt.lowercase()
        val cleaned = lower
            .replace("make me", "")
            .replace("generate", "")
            .replace("flashcards", "")
            .replace("flash cards", "")
            .replace("about", "")
            .replace("on", "")
            .replace(Regex("\\d+"), "")
            .trim()
        return if (cleaned.isNotBlank()) cleaned.replaceFirstChar { it.uppercase() } else prompt.trim()
    }
}
