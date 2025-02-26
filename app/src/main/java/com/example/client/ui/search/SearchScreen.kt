package com.example.client.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.service.LocalService
import com.example.client.ui.components.DropdownSelector

@Composable
fun SearchScreen(modifier: Modifier = Modifier,  onBackPressed: () -> Unit, localService: LocalService) {
    val uiState = localService.searchUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = onBackPressed, modifier = Modifier.align(Alignment.Start)) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.value.firstName,
            onValueChange = { localService.updateFirstName(it) },
            label = { Text("Enter First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownSelector(
            label = "Select City",
            options = uiState.value.cities,
            selectedOption = uiState.value.selectedCity,
            onOptionSelected = { localService.updateSelectedCitySearch(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                localService.getStudentByFirstNameAndCity(uiState.value.firstName, uiState.value.selectedCity)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.value.firstName.isNotBlank()
        ) {
            if (uiState.value.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        uiState.value.students?.let { s ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Student ID: ${s.studentID}")
                    Text("Name: ${s.firstName} ${s.lastName}")
                    Text("City: ${s.city}")
                    Text("Phone: ${s.phone}")
                    Text("Date of Birth: ${s.dateOfBirth}")

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Subjects:", fontWeight = FontWeight.Bold)

                    LazyColumn {
                        items(s.subjects) { subject ->
                            Text("${subject.name}: ${subject.score}")
                        }
                    }
                }
            }
        }
    }
}