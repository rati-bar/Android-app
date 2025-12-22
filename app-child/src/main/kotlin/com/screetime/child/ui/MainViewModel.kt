package com.screetime.child.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.screetime.child.data.model.Task
import com.screetime.child.data.model.TimeBalance
import com.screetime.child.data.repository.ScreenTimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val timeBalance: TimeBalance? = null,
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ScreenTimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Fetch time balance
                repository.getTimeBalance().collect { balance ->
                    _uiState.value = _uiState.value.copy(
                        timeBalance = balance,
                        isLoading = false
                    )
                }

                // Fetch tasks
                repository.getTasks().collect { tasks ->
                    _uiState.value = _uiState.value.copy(
                        tasks = tasks,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            val success = repository.completeTask(taskId)
            if (success) {
                // Reload data to get updated task status
                loadData()
            }
        }
    }

    fun refresh() {
        loadData()
    }
}
