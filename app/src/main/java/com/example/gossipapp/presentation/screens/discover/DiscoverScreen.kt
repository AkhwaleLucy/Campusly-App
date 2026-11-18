package com.example.gossipapp.presentation.screens.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gossipapp.data.local.entity.EventEntity
import com.example.gossipapp.data.local.entity.MarketplaceListingEntity
import com.example.gossipapp.data.local.entity.OpportunityEntity
import com.example.gossipapp.data.local.entity.StudyGroupEntity
import com.example.gossipapp.presentation.viewmodel.CampuslyUiState

@Composable
fun DiscoverScreen(
    uiState: CampuslyUiState,
    onToggleEventInterest: (EventEntity) -> Unit,
    onToggleGroupJoined: (StudyGroupEntity) -> Unit,
    onOpenAddMarketplaceListing: () -> Unit,
    onDeleteMarketplaceListing: (MarketplaceListingEntity) -> Unit,
    onMessageSeller: (MarketplaceListingEntity) -> Unit,
    onOpenSubmitEvent: () -> Unit,
    onOpenSubmitOpportunity: () -> Unit,
    onOpenSubmitGroup: () -> Unit,
    onAdminApproveEvent: (EventEntity) -> Unit,
    onAdminRejectEvent: (EventEntity) -> Unit,
    onAdminApproveOpportunity: (OpportunityEntity) -> Unit,
    onAdminRejectOpportunity: (OpportunityEntity) -> Unit,
    onAdminApproveGroup: (StudyGroupEntity) -> Unit,
    onAdminRejectGroup: (StudyGroupEntity) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Events, 1 = Marketplace, 2 = Opportunities, 3 = Groups, 4 = Admin Review (if admin)
    val isAdmin = uiState.user?.isAdmin == true

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Campus Life & Discover",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                if (isAdmin) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Admin Mode 🛡️",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.background,
                divider = {}
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Events (${uiState.events.size})") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Marketplace (${uiState.marketplaceListings.size})") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Opportunities (${uiState.opportunities.size})") })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Study Groups (${uiState.studyGroups.size})") })
                if (isAdmin) {
                    val pendingCount = uiState.pendingEvents.size + uiState.pendingOpportunities.size + uiState.pendingStudyGroups.size
                    Tab(selected = selectedTab == 4, onClick = { selectedTab = 4 }, text = { Text("Admin Review 🛡️ ($pendingCount)") })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> EventsTab(uiState.events, onOpenSubmitEvent, onToggleEventInterest)
                1 -> MarketplaceTab(uiState.marketplaceListings, uiState.user?.email ?: "", onOpenAddMarketplaceListing, onDeleteMarketplaceListing, onMessageSeller)
                2 -> OpportunitiesTab(uiState.opportunities, onOpenSubmitOpportunity)
                3 -> StudyGroupsTab(uiState.studyGroups, onOpenSubmitGroup, onToggleGroupJoined)
                4 -> if (isAdmin) AdminReviewTab(
                    pendingEvents = uiState.pendingEvents,
                    pendingOpps = uiState.pendingOpportunities,
                    pendingGroups = uiState.pendingStudyGroups,
                    onApproveEvent = onAdminApproveEvent,
                    onRejectEvent = onAdminRejectEvent,
                    onApproveOpp = onAdminApproveOpportunity,
                    onRejectOpp = onAdminRejectOpportunity,
                    onApproveGroup = onAdminApproveGroup,
                    onRejectGroup = onAdminRejectGroup
                )
            }
        }
    }
}

@Composable
fun EventsTab(
    events: List<EventEntity>,
    onOpenSubmit: () -> Unit,
    onToggleEventInterest: (EventEntity) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Campus Events", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Button(onClick = onOpenSubmit, shape = RoundedCornerShape(8.dp)) {
                Text("+ Submit Event")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (events.isEmpty()) {
            Text("No approved events yet. Submit an event for admin review!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(events) { event ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${event.category} • ${event.dateString}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                TextButton(onClick = { onToggleEventInterest(event) }) {
                                    Text(if (event.isInterested) "Interested ★" else "+ I'm Interested")
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Organizer: ${event.organizer} | Location: ${event.location}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = event.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MarketplaceTab(
    listings: List<MarketplaceListingEntity>,
    currentUserEmail: String,
    onOpenAddListing: () -> Unit,
    onDeleteListing: (MarketplaceListingEntity) -> Unit,
    onMessageSeller: (MarketplaceListingEntity) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") } // "All", "For Sale", "Wanted"

    val filtered = listings.filter { item ->
        selectedFilter == "All" || item.listingType.equals(selectedFilter, ignoreCase = true)
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Campus Buy & Sell",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Button(onClick = onOpenAddListing, shape = RoundedCornerShape(8.dp)) {
                Text("+ Post Listing")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedFilter == "All",
                onClick = { selectedFilter = "All" },
                label = { Text("All Items") }
            )
            FilterChip(
                selected = selectedFilter == "For Sale",
                onClick = { selectedFilter = "For Sale" },
                label = { Text("For Sale 🏷️") }
            )
            FilterChip(
                selected = selectedFilter == "Wanted",
                onClick = { selectedFilter = "Wanted" },
                label = { Text("Wanted 🔍") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Text("No marketplace listings in this section yet. Post a listing above!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filtered, key = { it.id }) { item ->
                    val isMine = item.sellerEmail == currentUserEmail

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (item.listingType == "Wanted") MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = item.listingType,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.listingType == "Wanted") MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = "KSh ${item.priceKes.toInt()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "${item.category} • Seller/Poster: ${item.sellerName} (${item.location})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!isMine) {
                                    Button(
                                        onClick = { onMessageSeller(item) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Message, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (item.listingType == "Wanted") "Message Buyer 💬" else "Message Seller 💬")
                                    }
                                } else {
                                    Text(
                                        text = "Your Listing",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    TextButton(
                                        onClick = { onDeleteListing(item) },
                                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Remove Listing")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OpportunitiesTab(
    opportunities: List<OpportunityEntity>,
    onOpenSubmit: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Career & Internships", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Button(onClick = onOpenSubmit, shape = RoundedCornerShape(8.dp)) {
                Text("+ Submit Opportunity")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (opportunities.isEmpty()) {
            Text("No approved opportunities yet. Submit an internship or fellowship for review!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(opportunities) { opportunity ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${opportunity.category} • Deadline: ${opportunity.deadlineString}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = opportunity.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Organization: ${opportunity.organization}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = opportunity.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudyGroupsTab(
    groups: List<StudyGroupEntity>,
    onOpenSubmit: () -> Unit,
    onToggleGroupJoined: (StudyGroupEntity) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Campus Study Groups", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Button(onClick = onOpenSubmit, shape = RoundedCornerShape(8.dp)) {
                Text("+ Submit Group")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (groups.isEmpty()) {
            Text("No approved study groups yet. Submit a new study group for review!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(groups) { group ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = group.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${group.courseName} • ${group.memberCount} Members",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = group.topicSummary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            Button(
                                onClick = { onToggleGroupJoined(group) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (group.isJoined) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = if (group.isJoined) "Joined ✓" else "Join",
                                    color = if (group.isJoined) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReviewTab(
    pendingEvents: List<EventEntity>,
    pendingOpps: List<OpportunityEntity>,
    pendingGroups: List<StudyGroupEntity>,
    onApproveEvent: (EventEntity) -> Unit,
    onRejectEvent: (EventEntity) -> Unit,
    onApproveOpp: (OpportunityEntity) -> Unit,
    onRejectOpp: (OpportunityEntity) -> Unit,
    onApproveGroup: (StudyGroupEntity) -> Unit,
    onRejectGroup: (StudyGroupEntity) -> Unit
) {
    val totalPending = pendingEvents.size + pendingOpps.size + pendingGroups.size

    Column {
        Text("Admin Moderation Review 🛡️", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Review pending submissions from students before publishing to shared feeds", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(12.dp))

        if (totalPending == 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "🎉 All caught up! No pending student submissions require review.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                if (pendingEvents.isNotEmpty()) {
                    item {
                        Text("Pending Events (${pendingEvents.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    items(pendingEvents) { event ->
                        AdminItemCard(
                            title = event.title,
                            subtitle = "${event.category} • Organizer: ${event.organizer}",
                            description = event.description,
                            onApprove = { onApproveEvent(event) },
                            onReject = { onRejectEvent(event) }
                        )
                    }
                }

                if (pendingOpps.isNotEmpty()) {
                    item {
                        Text("Pending Opportunities (${pendingOpps.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    items(pendingOpps) { opp ->
                        AdminItemCard(
                            title = opp.title,
                            subtitle = "${opp.category} • Org: ${opp.organization}",
                            description = opp.description,
                            onApprove = { onApproveOpp(opp) },
                            onReject = { onRejectOpp(opp) }
                        )
                    }
                }

                if (pendingGroups.isNotEmpty()) {
                    item {
                        Text("Pending Study Groups (${pendingGroups.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    items(pendingGroups) { group ->
                        AdminItemCard(
                            title = group.name,
                            subtitle = "Course: ${group.courseName}",
                            description = group.topicSummary,
                            onApprove = { onApproveGroup(group) },
                            onReject = { onRejectGroup(group) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminItemCard(
    title: String,
    subtitle: String,
    description: String,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onApprove, shape = RoundedCornerShape(8.dp)) {
                    Text("Approve ✓")
                }
                OutlinedButton(
                    onClick = onReject,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reject ✗")
                }
            }
        }
    }
}
