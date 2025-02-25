package com.example.client.ui.search

import android.util.Log
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.database.IStudentAPI
import com.example.common.model.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchScreen(modifier: Modifier = Modifier,  onBackPressed: () -> Unit, dbService: IStudentAPI) {
    var firstName by remember { mutableStateOf("") }
    var selectedCity by remember { mutableStateOf("Can Tho") }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var student by remember { mutableStateOf<Student?>(null) }

    val cities = listOf(
        "Can Tho", "Da Lat", "Da Nang", "HCM", "Hai Phong",
        "Hanoi", "Hue", "Nha Trang", "Quang Ninh", "Vung Tau"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Nút Back
        Button(onClick = onBackPressed, modifier = Modifier.align(Alignment.Start)) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nhập First Name
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("Enter First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown chọn thành phố
        Box {
            Button(onClick = { expanded = true }) {
                Text(selectedCity)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                cities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city) },
                        onClick = {
                            selectedCity = city
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading = true
                CoroutineScope(Dispatchers.IO).launch {
                    val result = dbService.getStudentByFirstNameAndCity(firstName, selectedCity)
                    withContext(Dispatchers.Main) {
                        if (result != null) {
                            student = result
                        } else {
                            Log.e("SearchScreen", "No student found for $firstName in $selectedCity")
                        }
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = firstName.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        student?.let { s ->
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