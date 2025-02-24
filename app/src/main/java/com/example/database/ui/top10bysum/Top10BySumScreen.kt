package com.example.database.ui.top10bysum

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.IStudentAPI
import com.example.database.data.model.Student
import com.example.database.ui.top10studentsubject.StudentCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun Top10BySumScreen(modifier: Modifier = Modifier, onBackPressed: () -> Unit, dbService: IStudentAPI) {
    val cities = listOf(
        "Can Tho", "Da Lat", "Da Nang", "HCM", "Hai Phong",
        "Hanoi", "Hue", "Nha Trang", "Quang Ninh", "Vung Tau"
    )
    val sumOptions = listOf("SumA", "SumB")

    var selectedCity by remember { mutableStateOf(cities.first()) }
    var selectedSumOption by remember { mutableStateOf(sumOptions.first()) }
    var students by remember { mutableStateOf<List<Student>?>(null) }
    var isLoading by remember { mutableStateOf(false) }

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
            options = sumOptions,
            selectedOption = selectedSumOption,
            onOptionSelected = { selectedSumOption = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        DropdownSelector(
            label = "Select City",
            options = cities,
            selectedOption = selectedCity,
            onOptionSelected = { selectedCity = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                isLoading = true
                students = null
                CoroutineScope(Dispatchers.IO).launch {
                    val result = if (selectedSumOption == sumOptions[0]) {
                        dbService.getTop10StudentSumAByCity(selectedCity)
                    } else {
                        dbService.getTop10StudentSumBByCity(selectedCity)
                    }
                    withContext(Dispatchers.Main) {
                        students = result
                        isLoading = false
                    }
                }
            }
        ) {
            Text("Get Top 10 for $selectedCity ($selectedSumOption)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        students?.let { list ->
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

@Composable
fun DropdownSelector(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, fontWeight = FontWeight.Bold)
        Box {
            Button(onClick = { expanded = true }) {
                Text(selectedOption)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}