// IStudentAPI.aidl
package com.example.database;

import com.example.database.data.model.Student;
import com.example.database.data.model.Subject;

interface IStudentAPI {
    boolean isDBInitialized();
    boolean initDB();
    List<Student> getStudentsWithPaging(int limit, int offset);
    List<Student> getTop10StudentBySubject(String subject);
    List<Student> getTop10StudentSumAByCity(String city);
    List<Student> getTop10StudentSumBByCity(String city);
    Student getStudentByFirstNameAndCity(String firstName, String city);
    List<Subject> getSubjectByStudentId(long studentId);
}