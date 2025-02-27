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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.client.service.LocalService
import com.example.common.model.Student
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun GetAllScreen(
    modifier: Modifier = Modifier,
    localService: LocalService,
    onBackPressed: () -> Unit
) {
    val uiState = localService.getAllUiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

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
                localService.getStudentPager()
            }
        ) {
            Text("Get All")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.value.isLoadingGetStudents) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

//        uiState.value.students?.let { students ->
//            LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                items(items = students, key = { it.studentID }) { student ->
//                    StudentItem(student)
//                }
//            }
//        }
//
//        LaunchedEffect(listState) {
//            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull() }
//                .distinctUntilChanged()
//                .collectLatest { lastItem ->
//                    if (lastItem != null && lastItem.index == uiState.value.students?.size?.minus(1)) {
//                        localService.getMoreStudentsPage()
//                    }
//                }
//        }

//        uiState.value.students?.let { students ->
//            LazyColumn(
//                state = listState,
//                modifier = Modifier.weight(1f),
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                items(students) { student ->
//                    StudentItem(
//                        student = student)
//                }
//            }
//        }

//        if (uiState.value.isLoadingMorePage) {
//            Box(
//                modifier = Modifier.fillMaxWidth(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        }
        uiState.value.pager?.let {
            StudentList(pager = it)
        }
    }
}

@Composable
fun StudentList(modifier: Modifier = Modifier, pager: Pager<Int, Student>) {
    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(
            lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.studentID }
        ) { index ->
            val message = lazyPagingItems[index]
            if (message != null) {
                StudentItem(message)
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun StudentItem(student: Student) {
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val paddingExpand = if (expanded) 96.dp else 24.dp

    Surface(color = MaterialTheme.colorScheme.primary) {
        Row(
            modifier = Modifier.padding(
                top = 24.dp,
                bottom = paddingExpand,
                start = 24.dp,
                end = 24.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                text = "${student.studentID}. ${student.firstName} ${student.lastName}"
            )
            ElevatedButton(onClick = {}) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Show More")
                }
            }
        }
    }
}