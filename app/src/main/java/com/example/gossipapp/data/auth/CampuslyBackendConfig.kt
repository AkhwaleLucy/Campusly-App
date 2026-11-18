package com.example.gossipapp.data.auth

/**
 * Backend role and access control configuration manager.
 * Stores server-verified admin accounts and credentials.
 * Admin access is disabled by default until explicitly configured by backend configuration.
 */
object CampuslyBackendConfig {

    private val verifiedAdminAccountIds = mutableSetOf<String>()
    private val verifiedAdminEmails = mutableSetOf<String>()

    /**
     * Configures trusted admin account IDs and emails verified by backend server.
     */
    fun configureTrustedAdminAccounts(adminIds: Set<String>, adminEmails: Set<String> = emptySet()) {
        synchronized(this) {
            verifiedAdminAccountIds.clear()
            verifiedAdminAccountIds.addAll(adminIds)
            verifiedAdminEmails.clear()
            verifiedAdminEmails.addAll(adminEmails.map { it.lowercase() })
        }
    }

    /**
     * Checks if the user ID or email is configured as a verified admin on the backend.
     */
    fun isVerifiedAdmin(userId: String?, email: String?): Boolean {
        if (userId == null && email == null) return false
        synchronized(this) {
            val isIdMatch = userId != null && verifiedAdminAccountIds.contains(userId)
            val isEmailMatch = email != null && verifiedAdminEmails.contains(email.lowercase())
            return isIdMatch || isEmailMatch
        }
    }
}
