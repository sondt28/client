package com.example.client.ui

import android.app.Activity.BIND_AUTO_CREATE
import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.client.service.HomeUiState
import com.example.database.IStudentAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClientViewModel(private val application: Application) : AndroidViewModel(application) {
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    private var databaseService: IStudentAPI? = null

    private val databaseConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            databaseService = IStudentAPI.Stub.asInterface(p1)
            initDBIfNeed()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            databaseService = null
        }
    }

    private fun initDBIfNeed() {
        viewModelScope.launch(Dispatchers.IO) {
            _homeUiState.update { it.copy(isLoading = true) }

            if (!isDBInitialized()) {
                databaseService?.initDB()
            }

            _homeUiState.update { it.copy(isLoading = false) }
        }
    }

    private fun isDBInitialized(): Boolean {
        return databaseService!!.isDBInitialized()
    }

    fun bindDatabaseService() {
        Intent(INTENT_DATABASE_SERVICE).also {
            it.setPackage(IStudentAPI::class.java.`package`?.name)
            application.applicationContext.bindService(it, databaseConnection, BIND_AUTO_CREATE)
        }
    }

    fun unbindDatabaseService() {
        application.applicationContext.unbindService(databaseConnection)
    }

    companion object {
        private const val INTENT_DATABASE_SERVICE = "istudentapi"
    }
}

