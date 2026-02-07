package com.smartnotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartnotes.data.database.AppDatabase
import com.smartnotes.data.repository.FolderRepository
import com.smartnotes.data.repository.NoteRepository
import com.smartnotes.data.repository.TagRepository
import com.smartnotes.ui.screens.NoteEditorScreen
import com.smartnotes.ui.screens.NoteListScreen
import com.smartnotes.ui.theme.SmartNotesTheme
import com.smartnotes.viewmodel.NoteEditorViewModel
import com.smartnotes.viewmodel.NoteListViewModel

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var noteRepository: NoteRepository
    private lateinit var folderRepository: FolderRepository
    private lateinit var tagRepository: TagRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        database = AppDatabase.getDatabase(applicationContext)
        noteRepository = NoteRepository(database.noteDao(), database.tagDao())
        folderRepository = FolderRepository(database.folderDao())
        tagRepository = TagRepository(database.tagDao())

        setContent {
            SmartNotesTheme {
                SmartNotesApp(
                    noteRepository = noteRepository,
                    folderRepository = folderRepository,
                    tagRepository = tagRepository
                )
            }
        }
    }
}

@Composable
fun SmartNotesApp(
    noteRepository: NoteRepository,
    folderRepository: FolderRepository,
    tagRepository: TagRepository
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "note_list"
    ) {
        composable("note_list") {
            val viewModel = NoteListViewModel(
                noteRepository = noteRepository,
                folderRepository = folderRepository,
                tagRepository = tagRepository
            )
            
            NoteListScreen(
                viewModel = viewModel,
                onNoteClick = { noteId ->
                    navController.navigate("note_editor/$noteId")
                },
                onCreateNote = {
                    navController.navigate("note_editor/0")
                }
            )
        }
        
        composable(
            route = "note_editor/{noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId") ?: 0L
            val viewModel = NoteEditorViewModel(
                noteRepository = noteRepository,
                context = context
            )
            
            NoteEditorScreen(
                viewModel = viewModel,
                noteId = if (noteId > 0) noteId else null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
