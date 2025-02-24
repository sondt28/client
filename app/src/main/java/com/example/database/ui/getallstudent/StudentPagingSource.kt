package com.example.database.ui.getallstudent

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.database.IStudentAPI
import com.example.database.data.model.Student
import kotlinx.coroutines.delay

class StudentPagingSource(private val dbService: IStudentAPI) : PagingSource<Int, Student>() {
    override fun getRefreshKey(state: PagingState<Int, Student>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(10) ?: anchorPage?.nextKey?.minus(10)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Student> {
        return try {
            val currentPosition = params.key ?: 0
            val response = dbService.getStudentsWithPaging(10, currentPosition)
            val nextKey = if (response.isEmpty()) null else currentPosition + 10

            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}