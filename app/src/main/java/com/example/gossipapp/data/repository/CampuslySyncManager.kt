package com.example.gossipapp.data.repository

import com.example.gossipapp.data.local.dao.CampuslyDao
import com.example.gossipapp.data.local.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Shared Backend Network Data Sync Manager
 * Synchronizes shared campus data (Forum Broadcasts, Replies, Marketplace, Private Messages,
 * Approved Events/Opportunities/Study Groups) between local Room cache and shared network store.
 */
class CampuslySyncManager(private val dao: CampuslyDao) {

    // Shared Network In-Memory Store (simulating remote server shared backend across accounts)
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

    suspend fun syncWithSharedBackend() = withContext(Dispatchers.IO) {
        // Fetch shared network messages and sync to local Room DB
        sharedForumMessages.forEach { dao.insertForumMessage(it) }
        sharedForumReplies.forEach { dao.insertForumReply(it) }
        sharedMarketplaceListings.forEach { dao.insertMarketplaceListing(it) }
        sharedPrivateConversations.forEach { dao.insertConversation(it) }
        sharedPrivateMessages.forEach { dao.insertPrivateMessage(it) }
        sharedEvents.forEach { dao.insertEvent(it) }
        sharedOpportunities.forEach { dao.insertOpportunity(it) }
        sharedStudyGroups.forEach { dao.insertStudyGroup(it) }
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

    suspend fun sendPrivateMessage(msg: PrivateMessageEntity) = withContext(Dispatchers.IO) {
        val id = dao.insertPrivateMessage(msg)
        val saved = msg.copy(id = id)
        synchronized(sharedPrivateMessages) { sharedPrivateMessages.add(saved) }
    }
}
