# Smart Notes - AI-Powered Note Taking App

A native Android application featuring advanced note-taking capabilities with voice dictation, handwriting recognition, and intelligent math processing.

## Features

### Core Functionality
- **Multiple Input Methods**
  - Typed text input
  - Voice dictation with spoken math interpretation
  - Handwriting recognition using ML Kit
  - Drawing mode for freehand sketches

### Math Processing
- Spoken math interpretation ("two by three" → 2/3, "two into three" → 2×3)
- Automatic math expression detection and solving
- Real-time formula rendering with KaTeX
- Support for basic arithmetic, powers, and roots

### Organization
- Folder-based organization
- Multi-tag support for notes
- Full-text search with FTS5
- Sort by date or title
- Filter by folder or tag

### Export Options
- Export as PDF
- Export as text file
- Export as image

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Repository pattern
- **Database**: Room with FTS5 for full-text search
- **ML/AI**: Google ML Kit for handwriting recognition
- **Math Processing**: mXparser library
- **PDF Generation**: iText7
- **Min SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)

## Architecture

```
app/
├── data/
│   ├── entities/     # Room database entities
│   ├── dao/          # Data Access Objects
│   ├── database/     # Database configuration
│   └── repository/   # Repository layer
├── services/
│   ├── VoiceInputService.kt          # Speech recognition
│   ├── HandwritingRecognitionService.kt # ML Kit integration
│   ├── MathProcessingService.kt      # Math parsing and solving
│   └── ExportService.kt              # Export functionality
├── viewmodel/        # ViewModels for screens
├── ui/
│   ├── screens/      # Composable screens
│   ├── components/   # Reusable UI components
│   └── theme/        # App theming
└── MainActivity.kt   # Main entry point
```

## Key Components

### Voice Input
- Uses Android SpeechRecognizer API
- Real-time transcription
- Spoken math interpretation (e.g., "two plus three equals" → "2 + 3 =")

### Handwriting Recognition
- Google ML Kit Digital Ink Recognition
- Converts drawings to text
- Supports mathematical symbols

### Math Processing
- Automatic detection of mathematical expressions
- Real-time evaluation and solving
- LaTeX rendering support
- Spoken math parser for natural language math input

### Database Schema
- **Notes**: id, title, content, folderId, tags, timestamps, drawing/math flags
- **Folders**: id, name, createdAt
- **Tags**: name, usageCount
- **FTS**: Full-text search index for notes

## Permissions

- `RECORD_AUDIO`: Required for voice input
- `WRITE_EXTERNAL_STORAGE`: Required for export (Android 9 and below)
- `INTERNET`: Required for KaTeX CDN (math rendering)

## Building

1. Open project in Android Studio
2. Sync Gradle dependencies
3. Run on Android 8.0+ device or emulator

```bash
./gradlew assembleDebug
```

**Finding the APK:** After building, the APK will be located at `app/build/outputs/apk/debug/app-debug.apk` (for debug builds) or `app/build/outputs/apk/release/app-release.apk` (for release builds).

For detailed information about APK locations, build variants, and installation, see [APK_LOCATION_GUIDE.md](APK_LOCATION_GUIDE.md).

## Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumentation tests:
```bash
./gradlew connectedAndroidTest
```

## Future Enhancements

- Cloud sync capability
- Collaborative note editing
- Advanced handwriting to text with custom training
- Offline math solving improvements
- Dark mode support
- Widget support
- Note sharing
- Backup/restore functionality

## License

This project is provided as-is for educational and demonstration purposes.
