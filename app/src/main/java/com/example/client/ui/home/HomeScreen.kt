package com.example.client.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.ui.ClientDestination
import com.example.client.ui.ClientViewModel
import com.example.client.ui.GetAllScreen
import com.example.client.ui.SearchScreen
import com.example.client.ui.Top10BySubjectScreen
import com.example.client.ui.Top10BySumScreen
import com.example.client.ui.components.ButtonItem
import com.example.client.ui.components.LoadingScreen

@Composable
fun HomeScreen(
    shareViewModel: ClientViewModel,
    onDestinationClick: (ClientDestination) -> Unit
) {
    val uiState = shareViewModel.homeUiState.collectAsStateWithLifecycle()

    if (uiState.value.isLoading) {
        LoadingScreen("Initializing database...")
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ButtonItem(text = "Get ALL", onClick = { onDestinationClick(GetAllScreen) })
            ButtonItem(text = "Top 10 by subject", onClick = { onDestinationClick(Top10BySubjectScreen) })
            ButtonItem(text = "Top 10 by Sum", onClick = { onDestinationClick(Top10BySumScreen) })
            ButtonItem(text = "Search", onClick = { onDestinationClick(SearchScreen) })
        }
    }
}