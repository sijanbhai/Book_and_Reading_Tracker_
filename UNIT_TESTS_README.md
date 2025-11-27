# Unit Tests for Book and Reading Tracker App

## Overview
This document describes the comprehensive unit tests created for all ViewModels in the Book and Reading Tracker application.

## Test Files Created

### 1. DashboardViewModelTest.kt
- **Location**: `app/src/test/java/com/sijan/bookandreadingtracker/presentation/viewmodel/DashboardViewModelTest.kt`
- **Tests Coverage**:
  - Initial state validation
  - Successful data loading from multiple sources
  - Loading state management
  - Recent finished books limitation (max 5)
  - Dashboard data refresh functionality
  - Total pages read calculation
  - Empty state handling for current reads and finished books

### 2. LibraryViewModelTest.kt
- **Location**: `app/src/test/java/com/sijan/bookandreadingtracker/presentation/viewmodel/LibraryViewModelTest.kt`
- **Tests Coverage**:
  - Initial state validation
  - Successful library books loading
  - Search functionality by title and author
  - Case-insensitive search
  - Search query clearing
  - Search with no matches
  - Library refresh functionality
  - Empty library handling
  - Search state persistence after refresh
  - Partial match filtering

### 3. RecommendationViewModelTest.kt
- **Location**: `app/src/test/java/com/sijan/bookandreadingtracker/presentation/viewmodel/RecommendationViewModelTest.kt`
- **Tests Coverage**:
  - Initial state validation
  - Successful recommendations loading
  - Error handling with custom messages
  - Generic error message handling
  - Book search functionality
  - Search error handling
  - Blank/whitespace query handling
  - Adding books to library
  - Error clearing
  - Empty state handling
  - Search query state management
  - Loading state management

### 4. BookDetailsViewModelTest.kt
- **Location**: `app/src/test/java/com/sijan/bookandreadingtracker/presentation/viewmodel/BookDetailsViewModelTest.kt`
- **Tests Coverage**:
  - Initial state validation
  - Successful book details loading
  - Book not found error handling
  - Personal note updates
  - Toggle library status (add/remove)
  - Reading progress updates
  - SavedStateHandle bookId extraction
  - Invalid/null bookId handling
  - Book entity property mapping
  - Edge cases (0 pages, empty notes)

### 5. ReaderViewModelTest.kt
- **Location**: `app/src/test/java/com/sijan/bookandreadingtracker/presentation/viewmodel/ReaderViewModelTest.kt`
- **Tests Coverage**:
  - Initial state validation
  - Successful book loading
  - Book not found error handling
  - Page change functionality
  - Page count boundary enforcement
  - Next/previous page navigation
  - Page boundary handling (0 and max pages)
  - Null book handling
  - SavedStateHandle bookId extraction
  - Invalid/null bookId handling
  - Current page initialization from book progress
  - Multiple page changes
  - Zero page count handling

## Test Dependencies Added

The following test dependencies have been added to `app/build.gradle.kts`:

```kotlin
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
testImplementation("io.mockk:mockk:1.13.12")
testImplementation("app.cash.turbine:turbine:1.1.0")
testImplementation("androidx.arch.core:core-testing:2.2.0")
```

### Dependency Purposes:
- **kotlinx-coroutines-test**: Testing coroutines and flows with test dispatchers
- **mockk**: Kotlin-friendly mocking framework
- **turbine**: Flow testing library for easy StateFlow/Flow assertions
- **androidx.arch.core:core-testing**: InstantTaskExecutorRule for LiveData testing

## Running the Tests

### Prerequisites
1. Ensure Java Development Kit (JDK) is installed and JAVA_HOME is set
2. Sync Gradle dependencies in Android Studio or via command line

### Option 1: Using Android Studio
1. Open the project in Android Studio
2. Right-click on the `test` directory under `app/src/test`
3. Select "Run 'Tests in 'app''" or "Run 'Tests in 'app'' with Coverage"
4. View test results in the Run panel

### Option 2: Using Gradle Command Line
```bash
# Windows
gradlew.bat test

# macOS/Linux
./gradlew test
```

### Option 3: Run Specific Test Class
```bash
# Windows
gradlew.bat test --tests "com.sijan.bookandreadingtracker.presentation.viewmodel.DashboardViewModelTest"

# macOS/Linux
./gradlew test --tests "com.sijan.bookandreadingtracker.presentation.viewmodel.DashboardViewModelTest"
```

## Test Architecture

### Testing Approach
- **Framework**: JUnit 4
- **Mocking**: MockK for creating test doubles
- **Coroutines**: StandardTestDispatcher for deterministic coroutine execution
- **Flow Testing**: Turbine for testing StateFlow emissions
- **Pattern**: Arrange-Act-Assert (Given-When-Then)

### Key Testing Patterns Used

#### 1. Test Dispatcher Setup
```kotlin
@Before
fun setup() {
    Dispatchers.setMain(testDispatcher)
    // Initialize mocks
}

@After
fun tearDown() {
    Dispatchers.resetMain()
}
```

#### 2. Flow Testing with Turbine
```kotlin
viewModel.state.test {
    val state = awaitItem()
    assertEquals(expected, state.value)
}
```

#### 3. Coroutine Testing
```kotlin
fun `test name`() = runTest {
    // Test code with coroutines
    advanceUntilIdle()
}
```

## Test Coverage

### Coverage Areas:
✅ **Initial State**: All ViewModels have initial state validation tests  
✅ **Data Loading**: Success scenarios for all data fetching operations  
✅ **Error Handling**: Error cases with proper error message validation  
✅ **Business Logic**: All business rules and calculations  
✅ **Loading States**: Loading indicators during async operations  
✅ **Empty States**: Handling of empty data scenarios  
✅ **Edge Cases**: Boundary conditions, null handling, invalid inputs  
✅ **User Actions**: All user-triggered actions (search, refresh, navigation)  

### Test Statistics:
- **DashboardViewModel**: 8 test cases
- **LibraryViewModel**: 13 test cases
- **RecommendationViewModel**: 15 test cases
- **BookDetailsViewModel**: 15 test cases
- **ReaderViewModel**: 17 test cases
- **Total**: 68 comprehensive test cases

## Best Practices Implemented

1. **Descriptive Test Names**: Using backticks for readable test names
2. **Isolated Tests**: Each test is independent and self-contained
3. **Mocking**: External dependencies are mocked to ensure unit isolation
4. **Test Structure**: Clear Given-When-Then structure
5. **Assertions**: Multiple assertions to validate complete state
6. **Edge Cases**: Comprehensive edge case coverage
7. **Deterministic**: Tests use test dispatchers for predictable execution

## Continuous Integration

These tests are ready for CI/CD integration. Add the following to your CI pipeline:

```yaml
- name: Run Unit Tests
  run: ./gradlew test
  
- name: Generate Test Report
  run: ./gradlew testDebugUnitTest --continue
  
- name: Upload Test Results
  uses: actions/upload-artifact@v2
  with:
    name: test-results
    path: app/build/test-results
```

## Troubleshooting

### Issue: Dependencies not found
**Solution**: Sync Gradle dependencies
```bash
# Android Studio: File → Sync Project with Gradle Files
# Command line:
gradlew.bat --refresh-dependencies
```

### Issue: JAVA_HOME not set
**Solution**: Set JAVA_HOME environment variable to your JDK installation path

### Issue: Tests fail with "No tests found"
**Solution**: Ensure test files are in the correct directory (`app/src/test/java/...`)

### Issue: MockK errors
**Solution**: Ensure all mocked objects are properly initialized in `@Before` setup

## Maintenance

### Adding New Tests
1. Follow the existing test structure
2. Use descriptive test names with backticks
3. Include Given-When-Then comments
4. Mock all external dependencies
5. Use `runTest` for coroutine tests
6. Validate both success and error scenarios

### Updating Tests
When updating ViewModels:
1. Update corresponding test file
2. Add tests for new functionality
3. Update existing tests if behavior changes
4. Ensure all tests pass before committing

## Code Quality Metrics

To generate code coverage reports:
```bash
# Windows
gradlew.bat testDebugUnitTestCoverage

# macOS/Linux
./gradlew testDebugUnitTestCoverage
```

Reports will be generated at: `app/build/reports/coverage/`

## Contact & Support

For issues or questions about these tests:
1. Check this README first
2. Review the inline comments in test files
3. Refer to the testing framework documentation:
   - [JUnit 4](https://junit.org/junit4/)
   - [MockK](https://mockk.io/)
   - [Turbine](https://github.com/cashapp/turbine)
   - [Kotlin Coroutines Test](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/)

---

**Last Updated**: November 28, 2025  
**Test Framework Versions**:
- JUnit: 4.13.2
- MockK: 1.13.12
- Coroutines Test: 1.9.0
- Turbine: 1.1.0

