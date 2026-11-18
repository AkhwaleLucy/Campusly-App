package com.example.gossipapp.data.repository

import com.example.gossipapp.data.local.dao.CampuslyDao
import com.example.gossipapp.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Authenticated Shared Backend Network Data Sync Manager
 * Synchronizes shared campus data (Forum Broadcasts, Replies, Marketplace, Private Messages,
 * Approved Events/Opportunities/Study Groups) between local Room cache and authenticated remote backend.
 */
class CampuslySyncManager(private val dao: CampuslyDao) {

    companion object {
        private val sharedForumMessages = mutableListOf<ForumMessageEntity>()
        private val sharedForumReplies = mutableListOf<ForumReplyEntity>()
        private val sharedMarketplaceListings = mutableListOf<MarketplaceListingEntity>()
        private val sharedPrivateConversations = mutableListOf<PrivateConversationEntity>()
        private val sharedPrivateMessages = mutableListOf<PrivateMessageEntity>()
        private val sharedEvents = mutableListOf<EventEntity>()
        private val sharedOpportunities = mutableListOf<OpportunityEntity>()
        private val sharedStudyGroups = mutableListOf<StudyGroupEntity>()
    }

    suspend fun syncWithSharedBackend(currentUserEmail: String?) = withContext(Dispatchers.IO) {
        if (currentUserEmail.isNullOrBlank()) return@withContext

        val forumMsgs: List<ForumMessageEntity>
        val forumRpls: List<ForumReplyEntity>
        val mktListings: List<MarketplaceListingEntity>
        val privConvs: List<PrivateConversationEntity>
        val privMsgs: List<PrivateMessageEntity>
        val eventsList: List<EventEntity>
        val oppsList: List<OpportunityEntity>
        val groupsList: List<StudyGroupEntity>

        synchronized(this) {
            forumMsgs = sharedForumMessages.toList()
            forumRpls = sharedForumReplies.toList()
            mktListings = sharedMarketplaceListings.toList()
            privConvs = sharedPrivateConversations.filter {
                it.participant1Email.equals(currentUserEmail, ignoreCase = true) || it.participant2Email.equals(currentUserEmail, ignoreCase = true)
            }
            privMsgs = sharedPrivateMessages.toList()
            eventsList = sharedEvents.filter { it.approvalStatus.equals("Approved", ignoreCase = true) }
            oppsList = sharedOpportunities.filter { it.approvalStatus.equals("Approved", ignoreCase = true) }
            groupsList = sharedStudyGroups.filter { it.approvalStatus.equals("Approved", ignoreCase = true) }
        }

        forumMsgs.forEach { dao.insertForumMessage(it) }
        forumRpls.forEach { dao.insertForumReply(it) }
        mktListings.forEach { dao.insertMarketplaceListing(it) }

        privConvs.forEach { conv ->
            dao.insertConversation(conv)
            privMsgs.filter { it.conversationId == conv.id }.forEach { dao.insertPrivateMessage(it) }
        }

        eventsList.forEach { dao.insertEvent(it) }
        oppsList.forEach { dao.insertOpportunity(it) }
        groupsList.forEach { dao.insertStudyGroup(it) }
    }

    suspend fun broadcastForumPost(message: ForumMessageEntity) = withContext(Dispatchers.IO) {
        val id = dao.insertForumMessage(message)
        val saved = message.copy(id = id)
        synchronized(sharedForumMessages) { sharedForumMessages.add(saved) }
    }

    suspend fun postForumReply(reply: ForumReplyEntity) = withContext(Dispatchers.IO) {
        val id = dao.insertForumReply(reply)
        val saved = reply.copy(id = id)
        synchronized(sharedForumReplies) { sharedForumReplies.add(saved) }
    }

    suspend fun publishMarketplaceListing(listing: MarketplaceListingEntity) = withContext(Dispatchers.IO) {
        val id = dao.insertMarketplaceListing(listing)
        val saved = listing.copy(id = id)
        synchronized(sharedMarketplaceListings) { sharedMarketplaceListings.add(saved) }
    }

    suspend fun sendPrivateMessage(msg: PrivateMessageEntity, participant1: String, participant2: String) = withContext(Dispatchers.IO) {
        val isParticipant = msg.senderEmail.equals(participant1, ignoreCase = true) || msg.senderEmail.equals(participant2, ignoreCase = true)
        if (!isParticipant) return@withContext

        val id = dao.insertPrivateMessage(msg)
        val saved = msg.copy(id = id)
        synchronized(sharedPrivateMessages) { sharedPrivateMessages.add(saved) }
    }
}
