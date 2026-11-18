package com.example.gossipapp.presentation.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Auth : Screen("auth")
    object Main : Screen("main")
}

enum class BottomTab(val title: String) {
    HOME("Home"),
    PLANNER("Planner"),
    FORUM("Forum"),
    STUDY("Study"),
    DISCOVER("Discover"),
    PROFILE("Profile")
}
