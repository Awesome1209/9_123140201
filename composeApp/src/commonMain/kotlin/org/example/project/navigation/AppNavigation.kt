package org.example.project.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.example.project.ui.screens.AddNoteScreen
import org.example.project.ui.screens.AiAssistantScreen
import org.example.project.ui.screens.EditNoteScreen
import org.example.project.ui.screens.FavoritesScreen
import org.example.project.ui.screens.NoteDetailScreen
import org.example.project.ui.screens.NotesScreen
import org.example.project.ui.screens.ProfileScreen
import org.example.project.viewmodel.AiViewModel
import org.example.project.viewmodel.NotesViewModel
import org.example.project.viewmodel.PlatformViewModel
import org.example.project.viewmodel.ProfileViewModel

@Composable
fun AppNavigation(
    notesViewModel: NotesViewModel,
    profileViewModel: ProfileViewModel,
    platformViewModel: PlatformViewModel,
    aiViewModel: AiViewModel
) {
    val navController = rememberNavController()
    val notesUiState by notesViewModel.uiState.collectAsState()
    val platformUiState by platformViewModel.uiState.collectAsState()
    val aiUiState by aiViewModel.uiState.collectAsState()

    var selectedDetailNoteId by remember { mutableStateOf<Long?>(null) }
    var selectedEditNoteId by remember { mutableStateOf<Long?>(null) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute == Screen.Notes.route ||
        currentRoute == Screen.Favorites.route ||
        currentRoute == Screen.Ai.route ||
        currentRoute == Screen.Profile.route
    val showAddButton = currentRoute == Screen.Notes.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        },
        floatingActionButton = {
            if (showAddButton) {
                FloatingActionButton(onClick = { navController.navigate(Screen.AddNote.route) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add note"
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Notes.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Notes.route) {
                NotesScreen(
                    uiState = notesUiState,
                    isConnected = platformUiState.isConnected,
                    onSearchQueryChange = notesViewModel::onSearchQueryChange,
                    onCycleSort = notesViewModel::cycleSortOrder,
                    onSyncClick = notesViewModel::syncAllPending,
                    onImportRemoteClick = notesViewModel::importRemoteNotes,
                    onDismissMessage = notesViewModel::clearMessages,
                    onNoteClick = { noteId ->
                        selectedDetailNoteId = noteId
                        navController.navigate(Screen.NoteDetail.route)
                    },
                    onToggleFavorite = notesViewModel::toggleFavorite
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    uiState = notesUiState,
                    onFavoriteSearchQueryChange = notesViewModel::onFavoriteSearchQueryChange,
                    onCycleSort = notesViewModel::cycleSortOrder,
                    onNoteClick = { noteId ->
                        selectedDetailNoteId = noteId
                        navController.navigate(Screen.NoteDetail.route)
                    },
                    onToggleFavorite = notesViewModel::toggleFavorite
                )
            }

            composable(Screen.Ai.route) {
                AiAssistantScreen(
                    uiState = aiUiState,
                    onInputChange = aiViewModel::onInputChange,
                    onSend = aiViewModel::sendCurrentMessage,
                    onQuickPrompt = aiViewModel::sendQuickPrompt,
                    onImageInputChange = aiViewModel::onImageInputChange,
                    onAnalyzeImage = aiViewModel::analyzeImagePrompt,
                    onClearChat = aiViewModel::clearChat,
                    onClearToolResult = aiViewModel::clearToolResult,
                    onClearError = aiViewModel::clearError
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    profileViewModel = profileViewModel,
                    notesUiState = notesUiState,
                    platformUiState = platformUiState,
                    onSortOrderChange = notesViewModel::setSortOrder,
                    onSyncClick = notesViewModel::syncAllPending,
                    onImportRemoteClick = notesViewModel::importRemoteNotes,
                    onRefreshPlatformInfo = platformViewModel::refreshPlatformInfo
                )
            }

            composable(Screen.AddNote.route) {
                AddNoteScreen(
                    aiUiState = aiUiState,
                    onBack = { navController.popBackStack() },
                    onSuggestCategory = aiViewModel::suggestCategory,
                    onClearAiResult = aiViewModel::clearToolResult,
                    onSave = { title, content, category ->
                        notesViewModel.addNote(title, content, category)
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.NoteDetail.route) {
                val note = selectedDetailNoteId?.let { notesViewModel.getNoteById(it) }
                NoteDetailScreen(
                    note = note,
                    aiUiState = aiUiState,
                    onBack = { navController.popBackStack() },
                    onToggleFavorite = notesViewModel::toggleFavorite,
                    onEdit = { id ->
                        selectedEditNoteId = id
                        navController.navigate(Screen.EditNote.route)
                    },
                    onDelete = { id ->
                        notesViewModel.deleteNote(id)
                        navController.popBackStack()
                    },
                    onSummarize = { target -> aiViewModel.summarizeNote(target.title, target.content) },
                    onImprove = { target -> aiViewModel.improveNote(target.title, target.content) },
                    onClearAiResult = aiViewModel::clearToolResult
                )
            }

            composable(Screen.EditNote.route) {
                val note = selectedEditNoteId?.let { notesViewModel.getNoteById(it) }
                EditNoteScreen(
                    note = note,
                    aiUiState = aiUiState,
                    onBack = { navController.popBackStack() },
                    onSuggestCategory = aiViewModel::suggestCategory,
                    onClearAiResult = aiViewModel::clearToolResult,
                    onSave = { title, content, category ->
                        val id = selectedEditNoteId
                        if (id != null) {
                            notesViewModel.updateNote(
                                id = id,
                                title = title,
                                content = content,
                                category = category
                            )
                        }
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
