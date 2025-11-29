# Book and Reading Tracker

A full-featured Android application for tracking reading progress, managing a personal library, and discovering new books using the Google Books API.

## Features

### Dashboard
- **Current Reads**: View all books currently being read with progress indicators
- **Overall Statistics**: Track total pages read and finished books count
- **Recent Finished Books**: Quick access to recently completed books with ratings

### Library
- **Personal Collection**: Manage all books added to your library
- **Search & Filter**: Find books quickly with real-time search
- **Progress Tracking**: Visual progress bars showing reading completion
- **Empty State**: Suggests adding books from Discover when library is empty

### Discover
- **Google Books Integration**: Search millions of books from Google Books API
- **Recommendations**: Browse curated book suggestions
- **Quick Add**: One-tap to add books to your library
- **Book Details**: View ratings, descriptions, and publishing info

### Profile
- **User Authentication**: Register and login with email/password
- **Session Management**: Persistent login sessions
- **User Profile**: View profile information
- **Logout**: Secure logout functionality

### Book Details
- **Comprehensive Info**: Title, author, cover, rating, page count, published date
- **Personal Notes**: Add and edit private notes for each book
- **Reading Progress**: Track pages read with visual progress indicator
- **Library Management**: Add/remove books from library

### PDF Reader
- **Built-in Reader**: Read PDFs directly in the app
- **Page Navigation**: Navigate with next/previous buttons
- **Auto Progress**: Reading progress updates automatically
- **Page Counter**: Always know your current position

## Architecture

### Clean Architecture with MVVM

```
app/
├── data/                      # Data Layer
│   ├── local/                # Room Database
│   │   ├── BookEntity.kt     # Database entity
│   │   ├── BookDao.kt        # Data access object
│   │   ├── UserEntity.kt     # User entity
│   │   ├── UserDao.kt        # User data access
│   │   └── AppDatabase.kt    # Room database
│   ├── remote/               # Network
│   │   ├── GoogleBooksApiService.kt
│   │   └── dto/              # Data transfer objects
│   └── repository/
│       ├── BookRepositoryImpl.kt
│       └── AuthRepositoryImpl.kt
│
├── domain/                    # Domain Layer
│   ├── model/                # Domain models
│   │   └── Book.kt
│   ├── repository/           # Repository interfaces
│   │   ├── BookRepository.kt
│   │   └── AuthRepository.kt
│   └── usecase/              # Business logic
│       ├── GetLibraryBooksUseCase.kt
│       ├── GetCurrentReadsUseCase.kt
│       ├── SearchBooksUseCase.kt
│       ├── UpdateReadingProgressUseCase.kt
│       └── ...
│
├── presentation/              # Presentation Layer
│   ├── screens/              # Compose UI screens
│   │   ├── DashboardScreen.kt
│   │   ├── LibraryScreen.kt
│   │   ├── RecommendationScreen.kt
│   │   ├── ProfileScreen.kt
│   │   ├── BookDetailsScreen.kt
│   │   └── ReaderScreen.kt
│   ├── viewmodel/            # ViewModels
│   │   ├── DashboardViewModel.kt
│   │   ├── LibraryViewModel.kt
│   │   ├── RecommendationViewModel.kt
│   │   ├── ProfileViewModel.kt
│   │   ├── BookDetailsViewModel.kt
│   │   └── ReaderViewModel.kt
│   └── navigation/           # Navigation
│       ├── Screen.kt
│       └── NavGraph.kt
│
└── di/                        # Dependency Injection
    ├── DatabaseModule.kt     # Room DI
    ├── NetworkModule.kt      # Retrofit DI
    └── RepositoryModule.kt   # Repository DI
```

## Tech Stack

### Core
- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Material Design components
- **Coroutines & Flow** - Asynchronous programming

### Architecture
- **MVVM** - Presentation pattern
- **Clean Architecture** - Multi-layer architecture
- **Use Cases** - Domain business logic

### Dependency Injection
- **Hilt** - Dependency injection framework

### Database
- **Room** - Local SQLite database
- **KSP** - Kotlin Symbol Processing for annotations

### Network
- **Retrofit** - REST API client
- **OkHttp** - HTTP client
- **Gson** - JSON serialization

### Navigation
- **Navigation Compose** - Jetpack Navigation for Compose

### Image Loading
- **Coil** - Image loading library optimized for Compose

## Dependencies

All dependencies are managed in `gradle/libs.versions.toml`:

- Hilt 2.52
- Room 2.6.1
- Retrofit 2.11.0
- Navigation Compose 2.8.4
- Coil 2.7.0
- Coroutines 1.9.0
- Compose BOM 2025.11.01
- Lifecycle 2.10.0

## Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK 29 (minimum)
- Android SDK 36 (target/compile)

### Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd BookandReadingTracker
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project directory

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle files
   - If not, click "Sync Now" in the notification bar

4. **Run the app**
   - Select a device or emulator
   - Click the Run button or press Shift+F10

## Data Model

### BookEntity
```kotlin
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String,
    val publishedDate: String,
    val rating: Float,
    val pageCount: Int,
    val pdfUrl: String,
    val isInLibrary: Boolean,
    val pagesRead: Int,
    val personalNote: String
)
```

### UserEntity
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val email: String,
    val password: String,
    val createdAt: Long
)
```

### Computed Properties
- `progressPercent` - Reading progress percentage
- `isCurrentlyReading` - Whether actively reading
- `isFinished` - Whether book is completed

## Database Schema

### Room Database Configuration
- **Database Version**: 2
- **Database Name**: `book_tracker_db`
- **Entities**: BookEntity, UserEntity
- **DAOs**: BookDao, UserDao

### Repositories
- **BookRepository**: Manages book data and Google Books API integration
- **AuthRepository**: Handles user authentication and session management


## Testing

### Unit Tests
Located in `app/src/test/`:

#### ViewModel Tests (68 test cases)
- **DashboardViewModelTest** (8 tests) - Dashboard data loading, refresh, statistics
- **LibraryViewModelTest** (13 tests) - Library management, search, filtering
- **RecommendationViewModelTest** (15 tests) - Book search, recommendations, error handling
- **BookDetailsViewModelTest** (15 tests) - Book details, notes, progress tracking
- **ReaderViewModelTest** (17 tests) - PDF reading, page navigation, progress updates

### Instrumented Tests
Located in `app/src/androidTest/`:

#### UI Tests
- **DashboardScreenTest** - Dashboard UI components
- **LibraryScreenTest** - Library screen interactions
- **RecommendationScreenTest** - Search and discovery UI
- **ProfileScreenTest** - Authentication UI
- **BookDetailsScreenTest** - Book details display
- **ReaderScreenTest** - PDF reader UI

#### Database Tests
- **BookDaoTest** - Room database operations
- **BookRepositoryImplIntegrationTest** - Integration tests

### Running Tests

#### Run All Unit Tests
```bash
# Windows
gradlew.bat test

# macOS/Linux
./gradlew test
```

#### Run Specific Test Class
```bash
# Windows
gradlew.bat test --tests "DashboardViewModelTest"

# macOS/Linux
./gradlew test --tests "DashboardViewModelTest"
```

## Key Features Summary

✅ **Complete CRUD Operations** - Create, read, update, delete books  
✅ **Google Books API Integration** - Search and discover millions of books  
✅ **User Authentication** - Register, login, and profile management  
✅ **Reading Progress Tracking** - Track pages read with visual progress bars  
✅ **Personal Library Management** - Organize and search your book collection  
✅ **PDF Reader** - Built-in reader for reading books in-app  
✅ **Personal Notes** - Add private notes to each book  
✅ **Statistics Dashboard** - Track reading statistics and progress  
✅ **Offline First** - Room database for local data persistence  
✅ **Modern UI** - Material 3 design with Jetpack Compose  
✅ **Clean Architecture** - Separation of concerns with MVVM pattern  
✅ **Dependency Injection** - Hilt for scalable architecture  
✅ **Comprehensive Testing** - 68 unit tests + instrumented tests  

## License

This project is developed for educational purposes as part of CP3406 Assignment 2.

## Author

Created by Sijan Bhandari as a demonstration of modern Android development practices using Kotlin, Jetpack Compose, and Clean Architecture principles.

