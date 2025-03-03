package com.example.client.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.client.App.Companion.instance
import com.example.client.service.LocalService
import com.example.client.ui.components.LoadingScreen
import com.example.client.ui.theme.ClientTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ClientActivity : ComponentActivity() {
    private val mLocalService =  mutableStateOf<LocalService?>(null)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as LocalService.LocalBinder
            mLocalService.value = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mLocalService.value = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClientApp(
                localService =  mLocalService.value
            )
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            bindService()
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService()
    }

    private fun bindService() {
        Intent(this, LocalService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindService() {
        unbindService(connection)
    }
}

@Composable
fun ClientApp(
    localService: LocalService?
) {
    localService?.let {
        ClientTheme {
            val navController = rememberNavController()

            Scaffold { innerPadding ->
                ClientNavHost(
                    localService = localService,
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    } ?: LoadingScreen("Connecting to database...")
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}