package com.ngapp.testcontentprovider.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ngapp.testcontentprovider.R
import com.ngapp.testcontentprovider.data.CourseRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class CourseDetailViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = CourseRepository(application)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)
    private val deleteSuccessEventChannel = Channel<Unit>(Channel.BUFFERED)

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val deleteSuccessFlow: Flow<Unit>
        get() = deleteSuccessEventChannel.receiveAsFlow()

    fun delete(id: Long) {
        viewModelScope.launch {
            runCatching {
                repository.deleteCourse(id)
            }.onSuccess {
                deleteSuccessEventChannel.send(Unit)
            }.onFailure { t ->
                toastEventChannel.send(R.string.delete_error)
                Log.e("CourseDetailViewModel", "Course delete error", t)
            }
        }
    }
}