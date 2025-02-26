package com.example.client.data

import com.example.common.model.Student
import com.example.database.IStudentAPI

class StudentRepository(private val studentAPI: IStudentAPI) {
    fun getStudentsWithPaging(limit: Int, offset: Int): List<Student> {
        return studentAPI.getStudentsWithPaging(limit, offset)
    }

    fun getTop10StudentBySubject(subject: String): List<Student> {
        return studentAPI.getTop10StudentBySubject(subject)
    }
}