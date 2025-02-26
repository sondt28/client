package com.example.client.ui.top10bysum

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.service.LocalService
import com.example.client.ui.components.DropdownSelector
import com.example.client.ui.top10studentsubject.StudentCard

@Composable
fun Top10BySumScreen(modifier: Modifier = Modifier, onBackPressed: () -> Unit, localService: LocalService) {
    val uiState = localService.top10BySumUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Top 10 Students by City", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        DropdownSelector(
            label = "Select Sum Option",
            options = uiState.value.sumOptions,
            selectedOption = uiState.value.sumOptionSelected,
            onOptionSelected = { localService.updateSelectedSumOption(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownSelector(
            label = "Select City",
            options = uiState.value.cities,
            selectedOption = uiState.value.selectedCity,
            onOptionSelected = { localService.updateSelectedCity(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                localService.getTop10BySum(uiState.value.sumOptionSelected, uiState.value.selectedCity)
            }
        ) {
            Text("Get Top 10 for ${uiState.value.sumOptionSelected} ${uiState.value.selectedCity}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.value.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.value.students?.let { list ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(list) { student ->
                    StudentCard(student)
                }
            }
        }
    }
}

