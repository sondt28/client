package com.example.client;

import com.example.common.model.Student;
import com.example.common.model.Subject;

interface IClientDataTransfer {
    List<Student> getStudentsWithPaging(int limit, int offset);
    List<Student> getTop10StudentBySubject(String subject);
    List<Student> getTop10StudentSumAByCity(String city);
    List<Student> getTop10StudentSumBByCity(String city);
    Student getStudentByFirstNameAndCity(String firstName, String city);
    List<Subject> getSubjectByStudentId(long studentId);
}