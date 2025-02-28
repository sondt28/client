// IStudentAPI.aidl
package com.example.database;

import com.example.common.model.Student;
import com.example.common.model.Subject;
import com.example.common.model.StudentSimple;

interface IStudentAPI {
    boolean isDBInitialized();
    boolean initDB();
    List<StudentSimple> getStudentsWithPaging(int limit, int offset);
    List<Student> getTop10StudentBySubject(String subject);
    List<Student> getTop10StudentSumAByCity(String city);
    List<Student> getTop10StudentSumBByCity(String city);
    Student getStudentByFirstNameAndCity(String firstName, String city);
    List<Subject> getSubjectByStudentId(long studentId);
}