package com.example.client.ui.getallstudent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client.ui.ClientViewModel
import com.example.database.IStudentAPI
import com.example.common.model.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun GetAllScreen(
    modifier: Modifier = Modifier,
    shareViewModel: ClientViewModel,
    onBackPressed: () -> Unit
) {
//    var students by remember { mutableStateOf<List<Student>>(emptyList()) }
//    var pageNumber by remember { mutableStateOf(0) }
//    var isLoading by remember { mutableStateOf(false) }
//    var isLoadingTop by remember { mutableStateOf(false) }
//
//    val expandedStates = remember { mutableStateMapOf<Long, Boolean>() }
//    val loadingStates = remember { mutableStateMapOf<Long, Boolean>() }
//
//    val listState = rememberLazyListState()
//
//    val isAtBottom by remember {
//        derivedStateOf {
//            val lastItemIndex = students.size - 1
//            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == lastItemIndex
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(onClick = onBackPressed) {
//                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(text = "Get All Student", fontSize = 20.sp, fontWeight = FontWeight.Bold)
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            modifier = Modifier.align(Alignment.CenterHorizontally),
//            onClick = {
//                isLoadingTop = true
//                pageNumber = 0
//                CoroutineScope(Dispatchers.IO).launch {
//                    val result = dbService.getStudentsWithPaging(10, pageNumber)
//                    withContext(Dispatchers.Main) {
//                        students = result
//                        isLoadingTop = false
//                    }
//                }
//            }
//        ) {
//            Text("Get All")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // **Loading Indicator khi bấm "Get All"**
//        if (isLoadingTop) {
//            Box(
//                modifier = Modifier.fillMaxWidth(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        }
//
//        LazyColumn(
//            state = listState,
//            modifier = Modifier.weight(1f),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(students) { student ->
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp)
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = "${student.studentID}. ${student.firstName} ${student.lastName}",
//                            fontSize = 16.sp
//                        )
//                        if (student.subjects.isEmpty()) {
//                            Button(
//                                onClick = {
//                                    loadingStates[student.studentID] = true
//                                    CoroutineScope(Dispatchers.IO).launch {
//                                        val fullStudent =
//                                            dbService.getSubjectByStudentId(student.studentID)
//                                        withContext(Dispatchers.Main) {
//                                            student.subjects = fullStudent
//                                            expandedStates[student.studentID] = true
//                                            loadingStates[student.studentID] = false
//                                        }
//                                    }
//                                }
//                            ) {
//                                if (loadingStates[student.studentID] == true) {
//                                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
//                                } else {
//                                    Text("Show More")
//                                }
//                            }
//                        }
//                    }
//
//                    if (expandedStates[student.studentID] == true) {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(top = 8.dp)
//                        ) {
//                            Text("${student.subjects[0].name}: ${student.subjects[0].score}")
//                            Text("${student.subjects[1].name}: ${student.subjects[1].score}")
//                            Text("${student.subjects[2].name}: ${student.subjects[2].score}")
//                            Text("${student.subjects[3].name}: ${student.subjects[3].score}")
//                            Text("${student.subjects[4].name}: ${student.subjects[4].score}")
//                            Text("${student.subjects[5].name}: ${student.subjects[5].score}")
//                            Text("${student.subjects[6].name}: ${student.subjects[6].score}")
//                            Text("${student.subjects[7].name}: ${student.subjects[7].score}")
//                            Text("${student.subjects[8].name}: ${student.subjects[8].score}")
//                            Text("${student.subjects[9].name}: ${student.subjects[9].score}")
//                        }
//                    }
//                }
//            }
//
//            if (isLoading) {
//                item {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//            }
//        }
//    }
//
//    LaunchedEffect(isAtBottom) {
//        if (isAtBottom && !isLoading) {
//            isLoading = true
//            pageNumber += 10
//            CoroutineScope(Dispatchers.IO).launch {
//                val newStudents = dbService.getStudentsWithPaging(10, pageNumber)
//                withContext(Dispatchers.Main) {
//                    students = students + newStudents
//                    isLoading = false
//                }
//            }
//        }
//    }
}