package com.example.database

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.database.ui.components.LoadingScreen
import com.example.database.ui.theme.ClientTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClientActivity : ComponentActivity() {
    private val isLoading = mutableStateOf(true to "connecting service...")

    private var databaseService: IStudentAPI? = null
    private val databaseConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            databaseService = IStudentAPI.Stub.asInterface(p1)
            isLoading.value = false to ""
            lifecycleScope.launch {
                initDBIfNeeded()
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            databaseService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClientApp(isLoading = isLoading.value.first to isLoading.value.second, databaseService = databaseService)
        }
    }

    override fun onStart() {
        super.onStart()
        bindDatabaseService()
    }

    override fun onStop() {
        super.onStop()
        unbindService(databaseConnection)
    }

    private suspend fun initDBIfNeeded() {
        if (databaseService?.isDBInitialized() == false) {
            isLoading.value = true to "Initializing database..."

            withContext(Dispatchers.IO) {
                databaseService?.initDB()
            }

            withContext(Dispatchers.Main) {
                isLoading.value = false to ""
            }
        } else {
            isLoading.value = false to ""
        }
    }

    private fun bindDatabaseService() {
        Intent("istudentapi").also {
            it.setPackage(IStudentAPI::class.java.`package`?.name)
            bindService(it, databaseConnection, BIND_AUTO_CREATE)
        }
    }
}

@Composable
fun ClientApp(isLoading: Pair<Boolean, String>, databaseService: IStudentAPI? = null) {
    if (isLoading.first) {
        LoadingScreen(isLoading.second)
    } else {
        ClientTheme {
            val navController = rememberNavController()

            Scaffold { innerPadding ->
                ClientNavHost(
                    dbService = databaseService!!,
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}