package com.example.gossipapp

import com.example.gossipapp.data.ai.CampuslyAiService
import com.example.gossipapp.data.auth.CampuslyBackendConfig
import com.example.gossipapp.data.local.entity.GpaRecordEntity
import com.example.gossipapp.data.local.entity.NoteEntity
import com.example.gossipapp.data.local.entity.StudySessionEntity
import com.example.gossipapp.data.local.entity.TimetableEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.UUID

class CampuslyDomainUnitTest {

    @Test
    fun calculateCumulativeGpa_calculatesWeightedAverageCorrectly() {
        val records = listOf(
            GpaRecordEntity(semesterName = "Sem 1", gpaScore = 3.5, creditUnits = 18),
            GpaRecordEntity(semesterName = "Sem 2", gpaScore = 3.8, creditUnits = 18)
        )
        val expected = (3.5 * 18 + 3.8 * 18) / 36.0
        val actual = calculateGpa(records)
        assertEquals(expected, actual, 0.001)
    }

    @Test
    fun timetableConflictDetector_flagsOverlappingClassesOnSameDay() {
        val existing = TimetableEntity(
            courseCode = "APT2060",
            courseName = "DSA",
            instructor = "Dr. X",
            room = "Lab 1",
            dayOfWeek = 2,
            startTime = "10:00",
            endTime = "12:00"
        )

        val overlapping = TimetableEntity(
            courseCode = "IST2045",
            courseName = "Networks",
            instructor = "Prof. Y",
            room = "Hall A",
            dayOfWeek = 2,
            startTime = "11:30",
            endTime = "13:00"
        )

        val nonOverlapping = TimetableEntity(
            courseCode = "APT3080",
            courseName = "MIS",
            instructor = "Dr. Z",
            room = "Room 3",
            dayOfWeek = 2,
            startTime = "12:30",
            endTime = "14:00"
        )

        assertTrue(checkConflict(existing, overlapping))
        assertFalse(checkConflict(existing, nonOverlapping))
    }

    @Test
    fun pomodoroFocusTimeCalculator_excludesBreaksAndDeduplicatesSessions() {
        val session1Id = UUID.randomUUID().toString()
        val session2Id = UUID.randomUUID().toString()

        val sessions = listOf(
            StudySessionEntity(sessionId = session1Id, courseName = "DSA", topic = "Trees", durationMinutes = 25, isBreak = false),
            StudySessionEntity(sessionId = session1Id, courseName = "DSA", topic = "Trees", durationMinutes = 25, isBreak = false), // Duplicate sync
            StudySessionEntity(sessionId = session2Id, courseName = "Networks", topic = "Subnetting", durationMinutes = 50, isBreak = false),
            StudySessionEntity(sessionId = UUID.randomUUID().toString(), courseName = "Rest", topic = "Break", durationMinutes = 10, isBreak = true) // Break
        )

        val totalFocusMinutes = sessions.filter { !it.isBreak }.distinctBy { it.sessionId }.sumOf { it.durationMinutes }
        assertEquals(75, totalFocusMinutes)
    }

    @Test
    fun quietHours_evaluatesOvernightRangeCorrectly() {
        val quietStart = "22:00"
        val quietEnd = "07:00"

        val nightCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 30)
        }

        val earlyMorningCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 4)
            set(Calendar.MINUTE, 15)
        }

        val afternoonCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 14)
            set(Calendar.MINUTE, 0)
        }

        assertTrue(isQuietHours(quietStart, quietEnd, nightCal))
        assertTrue(isQuietHours(quietStart, quietEnd, earlyMorningCal))
        assertFalse(isQuietHours(quietStart, quietEnd, afternoonCal))
    }

    @Test
    fun trustedAdminCheck_restrictsAdminRoleExclusiveToConfiguredBackendAccounts() {
        val adminId = "admin-user-id-123"
        val adminEmail = "verified_admin@campusly.app"
        val studentEmail = "student@campusly.app"

        CampuslyBackendConfig.configureTrustedAdminAccounts(
            adminIds = setOf(adminId),
            adminEmails = setOf(adminEmail)
        )

        assertTrue(CampuslyBackendConfig.isVerifiedAdmin(adminId, adminEmail))
        assertFalse(CampuslyBackendConfig.isVerifiedAdmin("random-id", studentEmail))
    }

    @Test
    fun aiFlashcardGenerator_generatesStructuredCardsForDatabaseNormalization() {
        val result = CampuslyAiService.generateStructuredCardsLocally("Database Normalization", "database normalization", false, 7)
        val cards = result.first

        assertEquals(7, cards.size)
        assertTrue(cards.any { it.front.contains("1NF", ignoreCase = true) || it.front.contains("Normalization", ignoreCase = true) })
        assertTrue(cards.any { it.back.contains("redundancy", ignoreCase = true) || it.back.contains("atomic", ignoreCase = true) })
    }

    @Test
    fun aiFlashcardGenerator_handlesShortNoteContentWithExplanation() {
        val shortNote = NoteEntity(title = "Brief Note", content = "AES uses 128-bit block size.")
        val result = CampuslyAiService.generateStructuredCardsLocally(shortNote.title, shortNote.content, true, 5)

        assertNotNull(result.first)
        assertTrue(result.first.isNotEmpty())
        assertNotNull(result.second)
        assertTrue(result.second!!.contains("Note content provided"))
    }

    private fun isQuietHours(start: String, end: String, now: Calendar): Boolean {
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val startMinutes = parseTimeToMinutes(start)
        val endMinutes = parseTimeToMinutes(end)

        return if (startMinutes > endMinutes) {
            currentMinutes >= startMinutes || currentMinutes < endMinutes
        } else {
            currentMinutes in startMinutes until endMinutes
        }
    }

    private fun calculateGpa(records: List<GpaRecordEntity>): Double {
        if (records.isEmpty()) return 0.0
        val totalPoints = records.sumOf { it.gpaScore * it.creditUnits }
        val totalCredits = records.sumOf { it.creditUnits }
        if (totalCredits == 0) return 0.0
        return totalPoints / totalCredits
    }

    private fun checkConflict(t1: TimetableEntity, t2: TimetableEntity): Boolean {
        if (t1.dayOfWeek != t2.dayOfWeek) return false
        val s1 = parseTimeToMinutes(t1.startTime)
        val e1 = parseTimeToMinutes(t1.endTime)
        val s2 = parseTimeToMinutes(t2.startTime)
        val e2 = parseTimeToMinutes(t2.endTime)
        return maxOf(s1, s2) < minOf(e1, e2)
    }

    private fun parseTimeToMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        val h = parts[0].trim().toInt()
        val m = parts[1].trim().toInt()
        return h * 60 + m
    }
}
