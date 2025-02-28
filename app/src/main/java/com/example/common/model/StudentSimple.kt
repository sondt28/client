package com.example.common.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StudentSimple(
    val studentID: Long,
    val firstName: String,
    val lastName: String
) : Parcelable