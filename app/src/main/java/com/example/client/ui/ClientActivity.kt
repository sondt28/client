package com.example.client.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.client.App.Companion.instance
import com.example.client.ui.theme.ClientTheme

class ClientActivity : ComponentActivity() {
    private val shareViewModel = mutableStateOf(ClientViewModel(instance))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClientApp(
                shareViewModel = shareViewModel.value
            )
        }
    }

    override fun onStart() {
        super.onStart()
        shareViewModel.value.bindDatabaseService()
    }

    override fun onStop() {
        super.onStop()
        shareViewModel.value.unbindDatabaseService()
    }
}

@Composable
fun ClientApp(
    shareViewModel: ClientViewModel
) {
    ClientTheme {
        val navController = rememberNavController()

        Scaffold { innerPadding ->
            ClientNavHost(
                shareViewModel = shareViewModel,
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}