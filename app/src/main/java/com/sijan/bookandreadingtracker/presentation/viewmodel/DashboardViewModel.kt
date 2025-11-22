package com.sijan.bookandreadingtracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sijan.bookandreadingtracker.data.local.BookEntity
import com.sijan.bookandreadingtracker.domain.usecase.GetCurrentReadsUseCase
import com.sijan.bookandreadingtracker.domain.usecase.GetFinishedBooksUseCase
import com.sijan.bookandreadingtracker.domain.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val currentReads: List<BookEntity> = emptyList(),
    val recentFinishedBooks: List<BookEntity> = emptyList(),
    val finishedBooksCount: Int = 0,
    val totalPagesRead: Int = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getCurrentReadsUseCase: GetCurrentReadsUseCase,
    private val getFinishedBooksUseCase: GetFinishedBooksUseCase,
    private val repository: BookRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            combine(
                getCurrentReadsUseCase(),
                getFinishedBooksUseCase(),
                repository.getFinishedBooksCount(),
                repository.getTotalPagesRead()
            ) { currentReads, finishedBooks, finishedCount, totalPages ->
                DashboardState(
                    currentReads = currentReads,
                    recentFinishedBooks = finishedBooks.take(5),
                    finishedBooksCount = finishedCount,
                    totalPagesRead = totalPages,
                    isLoading = false
                )
            }.collect { newState ->
                _state.value = newState
            }
        }
    }

    fun refreshData() {
        loadDashboardData()
    }
}

