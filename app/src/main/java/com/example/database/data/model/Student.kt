package com.example.database.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Student(
    val city: String,
    val dateOfBirth: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val studentID: Long,
    var subjects: List<Subject>
) : Parcelable