# Smart Notes - Project Structure

## Overview
This is a native Android application built with Kotlin and Jetpack Compose implementing a comprehensive note-taking system with AI-powered features.

## Directory Structure

```
SmartNotes/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smartnotes/
│   │   │   │   ├── data/
│   │   │   │   │   ├── entities/         # Room database entities
│   │   │   │   │   │   ├── Note.kt       # Note entity with FTS support
│   │   │   │   │   │   ├── Folder.kt     # Folder for organizing notes
│   │   │   │   │   │   └── Tag.kt        # Tag entity with usage tracking
│   │   │   │   │   ├── dao/              # Data Access Objects
│   │   │   │   │   │   ├── NoteDao.kt    # CRUD + search operations
│   │   │   │   │   │   ├── FolderDao.kt
│   │   │   │   │   │   └── TagDao.kt
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── AppDatabase.kt    # Room database configuration
│   │   │   │   │   │   └── Converters.kt     # Type converters for lists
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── NoteRepository.kt  # Note business logic
│   │   │   │   │       ├── FolderRepository.kt
│   │   │   │   │       └── TagRepository.kt
│   │   │   │   ├── services/
│   │   │   │   │   ├── VoiceInputService.kt           # Speech recognition
│   │   │   │   │   ├── HandwritingRecognitionService.kt # ML Kit integration
│   │   │   │   │   ├── MathProcessingService.kt       # Math parsing & solving
│   │   │   │   │   └── ExportService.kt               # PDF/Text/Image export
│   │   │   │   ├── viewmodel/
│   │   │   │   │   ├── NoteListViewModel.kt   # List screen logic
│   │   │   │   │   └── NoteEditorViewModel.kt # Editor screen logic
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── NoteListScreen.kt  # Main list view
│   │   │   │   │   │   └── NoteEditorScreen.kt # Note editing view
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── DrawingCanvas.kt   # Canvas for handwriting
│   │   │   │   │   │   ├── MathRenderer.kt    # LaTeX/math display
│   │   │   │   │   │   └── NoteCard.kt        # Note list item
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   └── MainActivity.kt         # App entry point
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml        # String resources
│   │   │   │   │   ├── colors.xml         # Color palette
│   │   │   │   │   └── themes.xml         # App themes
│   │   │   │   ├── drawable/              # Icons and graphics
│   │   │   │   └── xml/                   # Backup and data extraction rules
│   │   │   └── AndroidManifest.xml        # App configuration & permissions
│   │   ├── test/                          # Unit tests
│   │   │   └── java/com/smartnotes/
│   │   │       └── MathProcessingServiceTest.kt
│   │   └── androidTest/                   # Instrumentation tests
│   │       └── java/com/smartnotes/
│   │           └── DatabaseTest.kt
│   ├── build.gradle.kts                   # App-level Gradle config
│   └── proguard-rules.pro                 # ProGuard configuration
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── build.gradle.kts                       # Project-level Gradle config
├── settings.gradle.kts                    # Gradle settings
├── gradle.properties                      # Gradle properties
├── gradlew                                # Gradle wrapper script
├── .gitignore
└── README.md
```

## Key Components

### Data Layer
- **Room Database**: Local persistence with SQLite
- **FTS5**: Full-text search for notes
- **Type Converters**: Handle list serialization
- **Repositories**: Abstract data sources from ViewModels

### Services Layer
- **VoiceInputService**: Manages speech recognition lifecycle
- **HandwritingRecognitionService**: ML Kit integration for ink recognition
- **MathProcessingService**: 
  - Spoken math parser (natural language to expressions)
  - Expression evaluator using mXparser
  - LaTeX converter for rendering
- **ExportService**: Multi-format export (PDF, text, image)

### UI Layer
- **Jetpack Compose**: Declarative UI
- **Material 3**: Modern design components
- **Navigation**: Single-activity architecture with Navigation Compose
- **MVVM Pattern**: ViewModels manage UI state

### Features Implementation

#### Voice Input
- Android SpeechRecognizer API
- Partial and final results streaming
- Error handling with user feedback
- Spoken math interpretation ("two plus three" → "2 + 3")

#### Handwriting
- Custom drawing canvas with gesture detection
- Path-based stroke rendering
- ML Kit Digital Ink Recognition
- Async recognition with flow state management

#### Math Processing
- Pattern matching for spoken math phrases
- Expression extraction from text
- Real-time evaluation
- LaTeX rendering via WebView + KaTeX CDN

#### Organization
- Folder hierarchy
- Multi-tag support with usage counting
- FTS5-based full-text search
- Sort by date/title
- Filter by folder/tag

## Dependencies

### Core
- Kotlin 1.9.20
- Compose BOM 2023.10.01
- Material 3
- Navigation Compose

### Data
- Room 2.6.1
- Coroutines 1.7.3

### ML/AI
- ML Kit Digital Ink Recognition 18.1.0
- mXparser 5.2.1 (math evaluation)

### Export
- iText7 7.2.5 (PDF generation)

### Testing
- JUnit 4.13.2
- Mockk 1.13.8
- Compose UI Testing
- Espresso

## Build Configuration

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34
- **Java Version**: 17
- **Kotlin Compiler Extension**: 1.5.4

## Permissions

Required:
- `RECORD_AUDIO`: Voice input
- `INTERNET`: KaTeX CDN for math rendering

Optional (Android 9 and below):
- `WRITE_EXTERNAL_STORAGE`: Export functionality

## Testing Strategy

### Unit Tests
- Math parsing and solving logic
- Spoken math interpretation
- Repository operations (with mocked DAOs)
- ViewModel state management

### Instrumentation Tests
- Database operations (CRUD)
- Room queries and FTS
- UI flows with Compose Testing
- Navigation scenarios

### Manual Testing Required
- Voice recognition (device-specific)
- Handwriting recognition
- Drawing canvas performance
- Export file generation
- Permission handling

## Development Commands

Build APK:
```bash
./gradlew assembleDebug
```

Run tests:
```bash
./gradlew test
./gradlew connectedAndroidTest
```

Install on device:
```bash
./gradlew installDebug
```

## Notes for Future Development

1. **ViewModel Instances**: Currently created per screen - consider using Hilt/Koin for DI
2. **Math Rendering**: WebView-based - could be replaced with native solution
3. **Drawing Storage**: Paths stored as strings - consider binary format for efficiency
4. **Offline Math**: mXparser is comprehensive but large - consider lighter alternatives
5. **Sync**: No cloud sync currently - Firebase/custom backend could be added
6. **Security**: No encryption - consider adding for sensitive notes
