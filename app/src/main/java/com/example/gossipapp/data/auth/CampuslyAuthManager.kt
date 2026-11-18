package com.example.gossipapp.data.auth

import com.example.gossipapp.data.local.dao.CampuslyDao
import com.example.gossipapp.data.local.entity.UserEntity
import java.security.MessageDigest
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class CampuslyAuthManager(
    private val dao: CampuslyDao
) {
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
        if (password.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters long.")
        }

        val existing = dao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return AuthResult.Error("An account with email '$cleanEmail' already exists. Please Sign In or Reset Password.")
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

        val user = dao.getUserByEmail(cleanEmail)
            ?: return AuthResult.Error("Unregistered email address ($cleanEmail). No account found. Please Sign Up first.")

        if (user.passwordHash.isBlank()) {
            return AuthResult.Error("Account legacy session expired or incomplete. Please reset your password to set up credentials.")
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

    suspend fun resetPassword(email: String, newPassword: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank()) {
            return AuthResult.Error("Please enter your email address.")
        }
        val user = dao.getUserByEmail(cleanEmail)
            ?: return AuthResult.Error("No registered account found for $cleanEmail.")

        if (newPassword.length < 6) {
            return AuthResult.Error("New password must be at least 6 characters long.")
        }

        val updatedUser = user.copy(passwordHash = hashPassword(newPassword))
        dao.insertUser(updatedUser)
        return AuthResult.Success(updatedUser)
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
