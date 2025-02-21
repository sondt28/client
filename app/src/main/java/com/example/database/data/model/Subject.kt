package com.example.database.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Subject(
    val name: String,
    val score: Long,
    val studentID: Long
): Parcelable