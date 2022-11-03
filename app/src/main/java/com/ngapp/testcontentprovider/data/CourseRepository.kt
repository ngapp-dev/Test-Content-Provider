package com.ngapp.testcontentprovider.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class CourseRepository(
    private val context: Context
) {
    suspend fun getCourseList(): List<Course> = withContext(Dispatchers.IO) {
        val uri: Uri = Uri.parse(COURSES_URI)
        context.contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )?.use { cursor ->
            getCoursesFromCursor(cursor)
        }.orEmpty()
    }

    private fun getCoursesFromCursor(cursor: Cursor): List<Course> {
        if (cursor.moveToFirst().not()) return emptyList()
        val list = mutableListOf<Course>()
        do {

            val idIndex = cursor.getColumnIndex(COLUMN_COURSE_ID)
            val id = cursor.getLong(idIndex)
            val titleIndex =
                cursor.getColumnIndex(COLUMN_COURSE_TITLE)
            val title = cursor.getString(titleIndex).orEmpty()

            list.add(
                Course(
                    id = id,
                    title = title
                )
            )
        } while (cursor.moveToNext())

        return list
    }

    suspend fun saveCourse(title: String) =
        withContext(Dispatchers.IO) {
            val courseId = (1..100000).random().toLong()
            saveCourseTitle(courseId, title)
        }

    suspend fun updateCourse(course: Course) =
        withContext(Dispatchers.IO) {
            saveCourseTitle(course.id, course.title)
        }

    private fun saveCourseTitle(courseId: Long, title: String) {
        val uri: Uri = Uri.parse(COURSES_URI)
        val contentValues = ContentValues().apply {
            put(COLUMN_COURSE_ID, courseId)
            put(COLUMN_COURSE_TITLE, title)
        }
        context.contentResolver.insert(uri, contentValues)
    }

    suspend fun deleteCourse(courseId: Long) = withContext(Dispatchers.IO) {
        val uri: Uri = Uri.withAppendedPath(Uri.parse(COURSES_URI), courseId.toString())
        context.contentResolver.delete(uri, null, null);
    }

    suspend fun deleteAllCourses() = withContext(Dispatchers.IO) {
        context.contentResolver.delete(COURSES_URI.toUri(), null, null);
    }

    companion object {
        private const val AUTHORITIES = "com.ngapp.contentprovider.provider"
        private const val PATH_COURSES = "courses"

        private const val COLUMN_COURSE_ID = "id"
        private const val COLUMN_COURSE_TITLE = "title"

        const val COURSES_URI = "content://$AUTHORITIES/$PATH_COURSES/"
    }

}