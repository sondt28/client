// IStudentAPI.aidl
package com.example.database;

import com.example.database.data.model.Student;

interface IStudentAPI {
    int initDB();
    List<Student> getStudentsWithPaging(int limit, int offset);
    List<Student> getTop10StudentSumAByCity(String city);
    List<Student> getTop10StudentSumBByCity(String city);
    Student getStudentByFirstNameAndCity(String firstName, String city);
}