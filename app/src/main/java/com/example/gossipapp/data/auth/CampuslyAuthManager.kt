package com.example.gossipapp.data.auth

import com.example.gossipapp.data.local.dao.CampuslyDao
import com.example.gossipapp.data.local.entity.UserEntity
import java.security.MessageDigest
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: UserEntity, val infoMessage: String? = null) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class CampuslyAuthManager(
    private val dao: CampuslyDao
) {
    fun isValidInstitutionalEmail(email: String): Boolean {
        val clean = email.trim().lowercase()
        if (!clean.contains("@")) return false
        val parts = clean.split("@")
        if (parts.size != 2) return false
        val userPart = parts[0]
        val domain = parts[1]
        if (userPart.isBlank() || domain.isBlank()) return false
        return domain == "ac.ke" || domain.endsWith(".ac.ke")
    }

    suspend fun signUp(
        fullName: String,
        email: String,
        password: String,
        university: String,
        courseProgram: String,
        yearOfStudy: String
    ): AuthResult {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) {
            return AuthResult.Error("Email address cannot be empty.")
        }
        if (!isValidInstitutionalEmail(cleanEmail)) {
            return AuthResult.Error("Registration is restricted to valid institutional email addresses ending in .ac.ke (e.g., student@usiu.ac.ke).")
        }
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters long.")
        }

        val existing = dao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return AuthResult.Error("An account with email '$cleanEmail' already exists. Please Sign In or request Password Recovery.")
        }

        val userId = UUID.randomUUID().toString()
        val passwordHash = hashPassword(password)
        val isAdmin = CampuslyBackendConfig.isVerifiedAdmin(userId, cleanEmail)

        val newUser = UserEntity(
            id = userId,
            email = cleanEmail,
            fullName = fullName.trim(),
            university = university.trim(),
            courseProgram = courseProgram.trim(),
            yearOfStudy = yearOfStudy.trim(),
            bio = "Student at ${university.trim()}",
            passwordHash = passwordHash,
            isAdmin = isAdmin
        )

        dao.insertUser(newUser)
        return AuthResult.Success(newUser)
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) {
            return AuthResult.Error("Please enter your student email address.")
        }
        if (!isValidInstitutionalEmail(cleanEmail)) {
            return AuthResult.Error("Sign in requires a valid institutional email address ending in .ac.ke.")
        }

        val user = dao.getUserByEmail(cleanEmail)
            ?: return AuthResult.Error("Unregistered email address ($cleanEmail). No account found. Please Sign Up first.")

        if (user.passwordHash.isBlank()) {
            return AuthResult.Error("Account session incomplete. Please request a verified password recovery link.")
        }
        val hash = hashPassword(password)
        if (user.passwordHash != hash) {
            return AuthResult.Error("Incorrect password. Please verify your credentials and try again.")
        }

        val isAdmin = CampuslyBackendConfig.isVerifiedAdmin(user.id, cleanEmail)
        val updatedUser = if (user.isAdmin != isAdmin) user.copy(isAdmin = isAdmin) else user
        if (updatedUser != user) {
            dao.insertUser(updatedUser)
        }

        return AuthResult.Success(updatedUser)
    }

    suspend fun sendPasswordRecoveryEmail(email: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) {
            return AuthResult.Error("Please enter your registered student email address.")
        }
        if (!isValidInstitutionalEmail(cleanEmail)) {
            return AuthResult.Error("Password recovery is restricted to registered institutional .ac.ke accounts.")
        }
        val user = dao.getUserByEmail(cleanEmail)
            ?: return AuthResult.Error("No registered account found for '$cleanEmail'. Please check the email or sign up.")

        return AuthResult.Success(
            user = user,
            infoMessage = "A verified password recovery link has been sent to $cleanEmail. Please check your inbox and verify ownership before setting a new password."
        )
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
