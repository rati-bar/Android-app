package com.screetime.parent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.screetime.parent.data.model.ChildInfo
import com.screetime.parent.data.model.Task
import com.screetime.parent.data.repository.ParentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val childInfo: ChildInfo? = null,
    val pendingTasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val showRejectDialog: Task? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ParentRepository
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
                // Fetch child info
                repository.getChildInfo().collect { childInfo ->
                    _uiState.value = _uiState.value.copy(
                        childInfo = childInfo,
                        isLoading = false
                    )
                }

                // Fetch pending tasks
                repository.getPendingTasks().collect { tasks ->
                    _uiState.value = _uiState.value.copy(
                        pendingTasks = tasks,
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

    fun approveTask(taskId: String) {
        viewModelScope.launch {
            val success = repository.approveTask(taskId)
            if (success) {
                // Reload data to reflect changes
                loadData()
            }
        }
    }

    fun showRejectDialog(task: Task) {
        _uiState.value = _uiState.value.copy(showRejectDialog = task)
    }

    fun hideRejectDialog() {
        _uiState.value = _uiState.value.copy(showRejectDialog = null)
    }

    fun rejectTask(taskId: String, reason: String) {
        viewModelScope.launch {
            val success = repository.rejectTask(taskId, reason)
            if (success) {
                hideRejectDialog()
                loadData()
            }
        }
    }

    fun refresh() {
        loadData()
    }
}
