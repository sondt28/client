package com.example.database.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.database.ClientDestination
import com.example.database.GetAllScreen
import com.example.database.SearchScreen
import com.example.database.Top10BySubjectScreen
import com.example.database.Top10BySumScreen
import com.example.database.ui.components.ButtonItem

@Composable
fun HomeScreen(onDestinationClick: (ClientDestination) -> Unit) {
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