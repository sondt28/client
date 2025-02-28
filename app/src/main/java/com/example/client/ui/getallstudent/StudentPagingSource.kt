package com.example.client.ui.getallstudent

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.common.model.StudentSimple
import com.example.database.IStudentAPI

class StudentPagingSource(private val api: IStudentAPI, private val pageSize: Int) : PagingSource<Int, StudentSimple>() {
    override fun getRefreshKey(state: PagingState<Int, StudentSimple>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StudentSimple> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = api.getStudentsWithPaging(pageSize, nextPageNumber)
            Log.d("SonLN", "load: $response")
            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = nextPageNumber + pageSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}