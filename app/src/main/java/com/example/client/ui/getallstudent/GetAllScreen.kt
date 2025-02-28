package com.example.client.ui.getallstudent

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.client.service.GetAllUiState
import com.example.client.service.LocalService
import com.example.common.model.StudentSimple
import com.example.common.model.Subject
import kotlinx.coroutines.flow.Flow

@Composable
fun GetAllScreen(
    modifier: Modifier = Modifier,
    localService: LocalService,
    onBackPressed: () -> Unit
) {
    val uiState = localService.getAllUiState.collectAsStateWithLifecycle()
    val studentPagerFlow = localService.studentPagerFlow.collectAsState().value

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
            Text(text = "Get All Student", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                localService.initStudentPager(100)
            }
        ) {
            Text("Get All")
        }
        Spacer(modifier = Modifier.height(16.dp))

        studentPagerFlow?.let {
            StudentList(
                pager = studentPagerFlow,
                uiState = uiState.value,
                onExpandToggle = { localService.toggleExpand(it) })
        }
    }
}

@Composable
fun StudentList(
    modifier: Modifier = Modifier,
    pager: Flow<PagingData<StudentSimple>>,
    uiState: GetAllUiState,
    onExpandToggle: (Long) -> Unit
) {
    val lazyPagingItems = pager.collectAsLazyPagingItems()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(
            lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.studentID }
        ) { index ->
            val student = lazyPagingItems[index]
            if (student != null) {
                StudentItem(
                    student = student,
                    isExpanded = student.studentID in uiState.expandedItems,
                    isLoading = student.studentID in uiState.loadingItems,
                    subjects = uiState.subjects[student.studentID] ?: emptyList(),
                    onExpand = { onExpandToggle(it) }
                )
            }
        }

        when (val state = lazyPagingItems.loadState.refresh) { //FIRST LOAD
            is LoadState.Error -> {
            }

            is LoadState.Loading -> { // Loading UI
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp),
                            text = "Loading"
                        )

                        CircularProgressIndicator(color = Color.Black)
                    }
                }
            }

            else -> {}
        }

        when (val state = lazyPagingItems.loadState.append) { // Pagination
            is LoadState.Error -> {
                //TODO Pagination Error Item
                //state.error to get error message
            }

            is LoadState.Loading -> { // Pagination Loading UI
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(text = "Pagination Loading")

                        CircularProgressIndicator(color = Color.Black)
                    }
                }
            }

            else -> {}
        }
    }
}

@Composable
fun StudentItem(
    student: StudentSimple,
    isExpanded: Boolean,
    isLoading: Boolean,
    subjects: List<Subject>,
    onExpand: (Long) -> Unit
) {

    Surface(color = MaterialTheme.colorScheme.primary) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    text = "${student.studentID}. ${student.firstName} ${student.lastName}"
                )
                ElevatedButton(onClick = { onExpand(student.studentID) }, enabled = !isLoading) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (isExpanded) "Show Less" else "Show More")
                    }
                }
            }

            if (isExpanded) {
                Text(
                    text = subjects.joinToString(separator = ", ") { subject ->
                        "${subject.name}: ${subject.score}"
                    },
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}