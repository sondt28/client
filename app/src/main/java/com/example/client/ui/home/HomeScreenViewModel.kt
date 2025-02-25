package com.example.client.ui.home

import android.app.Activity.BIND_AUTO_CREATE
import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.lifecycleScope
import com.example.database.IStudentAPI
import kotlinx.coroutines.launch

class HomeScreenViewModel(private val application: Application) : AndroidViewModel(application) {
    private var databaseService: IStudentAPI? = null

    private val databaseConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            databaseService = IStudentAPI.Stub.asInterface(p1)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            databaseService = null
        }
    }

     fun bindDatabaseService() {
         Log.d("SonLN", "bindDatabaseService: $application")
        Intent("istudentapi").also {
            it.setPackage(IStudentAPI::class.java.`package`?.name)
            if (application.applicationContext.bindService(it, databaseConnection, BIND_AUTO_CREATE)) {
                Log.d("SonLN", "view model bindDatabaseService: success")
            } else {
                Log.d("SonLN", "view model bindDatabaseService: failed")
            }
        }
    }
}