package com.example.client.ui.getallstudent

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.common.model.Student
import com.example.database.IStudentAPI

class StudentPagingSource(private val api: IStudentAPI) : PagingSource<Int, Student>() {
    override fun getRefreshKey(state: PagingState<Int, Student>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Student> {
        return try {
            val nextPageNumber = params.key ?: 0

            val response = api.getStudentsWithPaging(10, nextPageNumber)
            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = nextPageNumber + 10
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}