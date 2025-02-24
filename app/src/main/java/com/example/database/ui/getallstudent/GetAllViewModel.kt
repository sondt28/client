package com.example.database.ui.getallstudent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.database.IStudentAPI

class GetAllViewModel(private val dbService: IStudentAPI) : ViewModel() {
    val getStudentsPage = Pager(
        config = PagingConfig(pageSize = 10, initialLoadSize = 10),
        pagingSourceFactory = { StudentPagingSource(dbService) }
    ).flow.cachedIn(viewModelScope)
}