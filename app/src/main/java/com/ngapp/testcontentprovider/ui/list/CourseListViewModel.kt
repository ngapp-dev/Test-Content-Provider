package com.ngapp.testcontentprovider.ui.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ngapp.testcontentprovider.R
import com.ngapp.testcontentprovider.data.Course
import com.ngapp.testcontentprovider.data.CourseRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CourseListViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = CourseRepository(application)
    private val courseMutableStateFlow = MutableStateFlow<List<Course>?>(null)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)

    val courseFlow: Flow<List<Course>?>
        get() = courseMutableStateFlow.asStateFlow()

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    fun deleteAllCourses() {
        viewModelScope.launch {
            runCatching {
                repository.deleteAllCourses()
            }.onSuccess {
                loadList()
                toastEventChannel.send(R.string.delete_success)
            }.onFailure { t ->
                toastEventChannel.send(R.string.delete_error)
                Log.e("CourseListViewModel", "Course delete error", t)
            }
        }
    }

    fun loadList() {
        viewModelScope.launch {
            runCatching {
                repository.getCourseList()
            }.onSuccess {
                courseMutableStateFlow.value = it
            }.onFailure { t ->
                courseMutableStateFlow.value = null
                Log.e("CourseListViewModel", "Course load list error", t)
            }
        }
    }

}