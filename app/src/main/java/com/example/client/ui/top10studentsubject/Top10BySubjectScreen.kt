package com.example.client.ui.top10studentsubject

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.client.service.LocalService
import com.example.common.model.Student
import com.example.common.model.StudentSimple

@Composable
fun Top10BySubjectScreen(modifier: Modifier = Modifier,  onBackPressed: () -> Unit, localService: LocalService) {
    val uiState = localService.top10BySubjectUiState.collectAsStateWithLifecycle()

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
            Text(text = "Top 10 Students", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        SubjectAssistChipGroup(
            subjects = uiState.value.subjects,
            selectedSubject = uiState.value.subjectSelected,
            onSubjectSelected = { localService.updateSelectedSubject(it) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { localService.getTop10BySubject(uiState.value.subjectSelected) }
        ) {
            Text("Get Top 10 Student for ${uiState.value.subjectSelected}")
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
                items(list) {
                    StudentCard(it)
                }
            }
        }
    }
}

@Composable
fun StudentCard(student: Student) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "${student.firstName} ${student.lastName}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = "ID: ${student.studentID}", fontSize = 16.sp)
            Text(text = "City: ${student.city}", fontSize = 16.sp)
            Text(text = "Date of Birth: ${student.dateOfBirth}", fontSize = 16.sp)
            Text(text = "Phone: ${student.phone}", fontSize = 16.sp)
        }
    }
}

@Composable
fun SubjectAssistChipGroup(
    subjects: List<String>,
    selectedSubject: String,
    onSubjectSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(subjects.size) { index ->
            AssistChip(
                onClick = { onSubjectSelected(subjects[index]) },
                label = { Text(subjects[index]) },
                leadingIcon = {
                    if (subjects[index] == selectedSubject) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.Blue
                        )
                    }
                },
                modifier = Modifier.padding(horizontal = 4.dp),
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (subjects[index] == selectedSubject) Color.Blue.copy(alpha = 0.2f) else Color.LightGray,
                    labelColor = if (subjects[index] == selectedSubject) Color.Blue else Color.Black
                )
            )
        }
    }
}