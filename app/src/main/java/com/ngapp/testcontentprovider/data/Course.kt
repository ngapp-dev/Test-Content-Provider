package com.ngapp.testcontentprovider.data

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Course(
    val id: Long,
    val title: String
): Parcelable
