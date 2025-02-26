package com.example.client.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.example.client.IClientDataTransfer
import com.example.common.model.Student
import com.example.common.model.Subject
import com.example.database.IStudentAPI

class DataTransferService : Service() {
    private val binder = object : IClientDataTransfer.Stub() {
        override fun getStudentsWithPaging(limit: Int, offset: Int): MutableList<Student> {
            return mutableListOf()
        }

        override fun getTop10StudentBySubject(subject: String?): MutableList<Student> {
            return mutableListOf()
        }

        override fun getTop10StudentSumAByCity(city: String?): MutableList<Student> {
            return mutableListOf()
        }

        override fun getTop10StudentSumBByCity(city: String?): MutableList<Student> {
            return mutableListOf()
        }

        override fun getStudentByFirstNameAndCity(firstName: String?, city: String?): Student {
            return Student("", "", "", "", "", 1L, mutableListOf())
        }

        override fun getSubjectByStudentId(studentId: Long): MutableList<Subject> {
            return mutableListOf()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}