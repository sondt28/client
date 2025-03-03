package com.example.client.ui.getallstudent

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.client.R
import com.example.client.service.GetAllUiState
import com.example.client.service.LocalService
import com.example.client.ui.components.LoadingScreen
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

    val lazyColumnListState = rememberLazyListState()

    val shouldPaginate = remember {
        derivedStateOf {
            val canPaginate = uiState.value.canPaginate
            val lastVisibleIndex = lazyColumnListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 5
            val totalItems = lazyColumnListState.layoutInfo.totalItemsCount

            canPaginate && lastVisibleIndex >= totalItems - 3
        }
    }

    LaunchedEffect(shouldPaginate.value) {
        if (shouldPaginate.value && uiState.value.paginationState == PaginationState.REQUEST_INACTIVE) {
            Log.d("SonLN", "load more")
            localService.getStudentsWithPaging(page = uiState.value.page)
        }
    }

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
                localService.clearPaging()
                localService.getStudentsWithPaging(page = uiState.value.page)
            }
        ) {
            Text("Get All")
        }
        Spacer(modifier = Modifier.height(16.dp))

        StudentList(
            lazyListState = lazyColumnListState,
            uiState = uiState.value,
            onExpandToggle = { localService.toggleExpand(it) })
    }
}

@Composable
fun StudentList(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    uiState: GetAllUiState,
    onExpandToggle: (Long) -> Unit
) {

    LazyColumn(state = lazyListState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(
            uiState.students.size,
            key = { uiState.students[it].studentID },
        ) { index ->
            val student = uiState.students[index]
            StudentItem(
                student = student,
                isExpanded = student.studentID in uiState.expandedItems,
                isLoading = student.studentID in uiState.loadingItems,
                subjects = uiState.subjects[student.studentID] ?: emptyList(),
                onExpand = { onExpandToggle(it) }
            )
        }
        when (uiState.paginationState) {
            PaginationState.REQUEST_INACTIVE -> {
            }

            PaginationState.FIRST_LOADING -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            PaginationState.PAGINATING -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            PaginationState.PAGINATION_EXHAUST -> {

            }

            PaginationState.EMPTY -> {
            }
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