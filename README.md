# 📚 Book and Reading Tracker

A full-featured Android application for tracking reading progress, managing a personal library, and discovering new books using the Google Books API.

## 🎯 Features

### 📊 Dashboard
- **Current Reads**: View all books currently being read with progress indicators
- **Overall Statistics**: Track total pages read and finished books count
- **Recent Finished Books**: Quick access to recently completed books with ratings

### 📖 Library
- **Personal Collection**: Manage all books added to your library
- **Search & Filter**: Find books quickly with real-time search
- **Progress Tracking**: Visual progress bars showing reading completion
- **Empty State**: Helpful prompt to add books when library is empty

### 🔍 Discover
- **Google Books Integration**: Search millions of books from Google Books API
- **Recommendations**: Browse curated book suggestions
- **Quick Add**: One-tap to add books to your library
- **Book Details**: View ratings, descriptions, and publishing info

### 📝 Book Details
- **Comprehensive Info**: Title, author, cover, rating, page count, published date
- **Personal Notes**: Add and edit private notes for each book
- **Reading Progress**: Track pages read with visual progress indicator
- **Library Management**: Add/remove books from library

### 📄 PDF Reader
- **Built-in Reader**: Read PDFs directly in the app
- **Page Navigation**: Navigate with next/previous buttons
- **Auto Progress**: Reading progress updates automatically
- **Page Counter**: Always know your current position

## 🏗️ Architecture

### Clean Architecture with MVVM

```
app/
├── data/                      # Data Layer
│   ├── local/                # Room Database
│   │   ├── BookEntity.kt     # Database entity
│   │   ├── BookDao.kt        # Data access object
│   │   └── AppDatabase.kt    # Room database
│   ├── remote/               # Network
│   │   ├── GoogleBooksApiService.kt
│   │   └── dto/              # Data transfer objects
│   └── repository/
│       └── BookRepositoryImpl.kt
│
├── domain/                    # Domain Layer
│   ├── model/                # Domain models
│   │   └── Book.kt
│   ├── repository/           # Repository interfaces
│   │   └── BookRepository.kt
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
│   │   ├── BookDetailsScreen.kt
│   │   └── ReaderScreen.kt
│   ├── viewmodel/            # ViewModels
│   │   ├── DashboardViewModel.kt
│   │   ├── LibraryViewModel.kt
│   │   ├── RecommendationViewModel.kt
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

## 🛠️ Tech Stack

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

## 📦 Dependencies

All dependencies are managed in `gradle/libs.versions.toml`:

- Hilt 2.51.1
- Room 2.6.1
- Retrofit 2.11.0
- Navigation Compose 2.8.4
- Coil 2.7.0
- Coroutines 1.9.0

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK 29 (minimum)
- Android SDK 35 (target)

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
   - Click the Run button (▶️) or press Shift+F10

## 🔑 API Configuration

The app uses the Google Books API, which doesn't require an API key for basic usage. If you need higher rate limits:

1. Get an API key from [Google Cloud Console](https://console.cloud.google.com/)
2. Enable the Books API
3. Add the key to `local.properties`:
   ```
   GOOGLE_BOOKS_API_KEY=your_api_key_here
   ```

## 📱 Screens

### Bottom Navigation
- **Dashboard** (Home Icon) - Reading statistics and current reads
- **Library** (List Icon) - Personal book collection
- **Discover** (Search Icon) - Browse and search books

### Additional Screens
- **Book Details** - Detailed book information
- **PDF Reader** - In-app PDF reading experience

## 🎨 UI/UX Features

- **Material 3 Design** - Modern, adaptive UI
- **Dark Mode Support** - Automatic theme switching
- **Edge-to-Edge Display** - Immersive full-screen experience
- **Smooth Animations** - Polished user interactions
- **Loading States** - Clear feedback during operations
- **Empty States** - Helpful messages when no data
- **Error Handling** - User-friendly error messages

## 📊 Data Model

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

### Computed Properties
- `progressPercent` - Reading progress percentage
- `isCurrentlyReading` - Whether actively reading
- `isFinished` - Whether book is completed

## 🔄 State Management

Each ViewModel manages UI state using Kotlin Flows:

```kotlin
data class LibraryState(
    val books: List<BookEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false
)
```

## 🧪 Testing

### Unit Tests
Located in `app/src/test/`:
- ViewModel tests
- Use case tests
- Repository tests

### Instrumented Tests
Located in `app/src/androidTest/`:
- Database tests
- UI tests

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 👨‍💻 Author

Created as a demonstration of modern Android development practices.

## 🙏 Acknowledgments

- **Google Books API** - Book data and cover images
- **Material Design** - Design guidelines and components
- **Android Jetpack** - Modern Android libraries

## 📞 Support

For questions or issues, please open an issue on the GitHub repository.

---

**Happy Reading! 📚**

