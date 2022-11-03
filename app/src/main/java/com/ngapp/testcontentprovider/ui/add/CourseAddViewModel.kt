package com.ngapp.testcontentprovider.ui.add

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ngapp.testcontentprovider.R
import com.ngapp.testcontentprovider.data.Course
import com.ngapp.testcontentprovider.data.CourseRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CourseAddViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = CourseRepository(application)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val saveSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val saveSuccessFlow: Flow<Unit>
        get() = saveSuccessEventChannel.receiveAsFlow()

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            runCatching {
                if (course.id == 0L) {
                    repository.saveCourse(course.title)
                } else {
                    repository.updateCourse(course)
                }
            }.onSuccess {
                saveSuccessEventChannel.send(Unit)
            }.onFailure { t ->
                Log.e("CourseAddViewModel", "course add error", t)
                toastEventChannel.send(R.string.add_course_error)
            }
        }
    }
}