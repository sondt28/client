package com.example.client.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.client.IClientDataTransfer
import com.example.common.model.StudentSimple
import com.example.common.model.Student
import com.example.common.model.Subject
import kotlinx.coroutines.runBlocking

class DataTransferService : Service() {
    private lateinit var localService: LocalService
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as LocalService.LocalBinder
            localService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }
    }

    private val binder = object : IClientDataTransfer.Stub() {
        override fun getStudentsWithPaging(limit: Int, offset: Int): MutableList<StudentSimple>? {
            localService.clearPaging()

            localService.updateInitialPaginationState()

            val first100Student = runBlocking {
                val students = localService.fetchStudentWithPaging(limit, offset)
                val canPaginate = students?.size == limit
                localService.updateStudentList(students?.take(10) ?: emptyList(), canPaginate)
                students
            }

            return first100Student?.toMutableList()
        }

        override fun getTop10StudentBySubject(subject: String): MutableList<Student>? {
            localService.getTop10BySubject(subject)
            return localService.top10BySubjectUiState.value.students?.toMutableList()
        }

        override fun getTop10StudentSumAByCity(city: String?): MutableList<Student>? {
            localService.getTop10BySum("SumA", city ?: "")
            return localService.top10BySumUiState.value.students?.toMutableList()
        }

        override fun getTop10StudentSumBByCity(city: String?): MutableList<Student>? {
            localService.getTop10BySum("SumB", city ?: "")
            return localService.top10BySumUiState.value.students?.toMutableList()
        }

        override fun getStudentByFirstNameAndCity(firstName: String?, city: String?): Student? {
            localService.getStudentByFirstNameAndCity(firstName ?: "", city ?: "")
            return localService.searchUiState.value.students
        }

        override fun getSubjectByStudentId(studentId: Long): MutableList<Subject> {
            return mutableListOf()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        if (!isBound) {
            Intent(this, LocalService::class.java).also {
                bindService(it, connection, BIND_AUTO_CREATE)
            }
        }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        this.unbindService(connection)
        return super.onUnbind(intent)
    }
}