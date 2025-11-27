# Quick Start Guide - Running Unit Tests

## ⚡ Prerequisites
- Java Development Kit (JDK) installed
- Android Studio (recommended) OR command line with Gradle

## 🚀 Quick Start

### Option 1: Android Studio (Recommended)
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Navigate to: `app/src/test/java/.../viewmodel/`
4. Right-click on any test file (e.g., `DashboardViewModelTest.kt`)
5. Select **"Run 'DashboardViewModelTest'"**
6. View results in the Run panel

### Option 2: Command Line

#### Run All Tests
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

## 📊 What's Been Created

### Test Files (68 test cases total)
```
app/src/test/java/com/sijan/bookandreadingtracker/presentation/viewmodel/
│
├── DashboardViewModelTest.kt      (8 tests)
├── LibraryViewModelTest.kt        (13 tests)
├── RecommendationViewModelTest.kt (15 tests)
├── BookDetailsViewModelTest.kt    (15 tests)
└── ReaderViewModelTest.kt         (17 tests)
```

### Documentation Files
```
├── UNIT_TESTS_README.md    (Comprehensive documentation)
├── TEST_SUMMARY.md         (Quick reference of all tests)
└── QUICK_START.md          (This file)
```

## ✅ What Each Test Covers

| ViewModel | Initial State | Data Loading | Error Handling | Business Logic |
|-----------|--------------|--------------|----------------|----------------|
| Dashboard | ✅ | ✅ | ✅ | ✅ |
| Library | ✅ | ✅ | ✅ | ✅ |
| Recommendation | ✅ | ✅ | ✅ | ✅ |
| BookDetails | ✅ | ✅ | ✅ | ✅ |
| Reader | ✅ | ✅ | ✅ | ✅ |

## 🔧 Dependencies Added

The following dependencies were added to `app/build.gradle.kts`:

```kotlin
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
testImplementation("io.mockk:mockk:1.13.12")
testImplementation("app.cash.turbine:turbine:1.1.0")
testImplementation("androidx.arch.core:core-testing:2.2.0")
```

## 📝 Test Example

Here's what a typical test looks like:

```kotlin
@Test
fun `initial state should have default values`() = runTest {
    // Given
    coEvery { getLibraryBooksUseCase() } returns flowOf(emptyList())

    // When
    viewModel = LibraryViewModel(getLibraryBooksUseCase)
    advanceUntilIdle()

    // Then
    viewModel.state.test {
        val state = awaitItem()
        assertTrue(state.books.isEmpty())
        assertFalse(state.isLoading)
    }
}
```

## 🎯 Test Coverage Summary

### DashboardViewModel
- ✅ Dashboard data loading
- ✅ Refresh functionality
- ✅ Statistics calculation
- ✅ Empty state handling

### LibraryViewModel
- ✅ Library books display
- ✅ Search by title/author
- ✅ Case-insensitive filtering
- ✅ Refresh functionality

### RecommendationViewModel
- ✅ Recommendations loading
- ✅ Book search
- ✅ Error handling
- ✅ Add to library

### BookDetailsViewModel
- ✅ Book details display
- ✅ Personal notes
- ✅ Library status toggle
- ✅ Reading progress

### ReaderViewModel
- ✅ Book reading
- ✅ Page navigation
- ✅ Progress tracking
- ✅ Boundary handling

## 🐛 Troubleshooting

### Problem: "JAVA_HOME is not set"
**Solution**: Set JAVA_HOME environment variable
```bash
# Windows (Command Prompt as Administrator)
setx JAVA_HOME "C:\Program Files\Java\jdk-XX"

# macOS/Linux
export JAVA_HOME=/path/to/jdk
```

### Problem: "Dependencies not found"
**Solution**: Sync Gradle in Android Studio
- File → Sync Project with Gradle Files

### Problem: Tests not found
**Solution**: Ensure files are in correct directory
- Should be: `app/src/test/java/...`
- NOT: `app/src/androidTest/java/...`

## 📚 More Information

For detailed information, see:
- **UNIT_TESTS_README.md** - Complete documentation
- **TEST_SUMMARY.md** - All test cases listed

## ✨ Key Features

✅ **68 comprehensive test cases** covering all ViewModels  
✅ **100% ViewModel coverage** - all 5 ViewModels tested  
✅ **4 coverage areas** - Initial state, Data loading, Error handling, Business logic  
✅ **Best practices** - Using MockK, Turbine, Coroutine Test  
✅ **Well documented** - Clear test names and structure  
✅ **CI/CD ready** - Can be integrated into build pipelines  

## 🎓 Testing Framework

- **JUnit 4**: Test framework
- **MockK**: Mocking library
- **Turbine**: Flow testing
- **Coroutines Test**: Async testing

## 💡 Tips

1. **Run tests frequently** during development
2. **Check coverage** to find untested code
3. **Keep tests updated** when changing ViewModels
4. **Use descriptive names** for new tests
5. **Follow existing patterns** for consistency

---

**Ready to test?** Just run: `gradlew.bat test` (Windows) or `./gradlew test` (macOS/Linux)

**Need help?** Check UNIT_TESTS_README.md for detailed documentation.

---

*Generated: November 28, 2025*  
*Status: ✅ All tests ready to run*

