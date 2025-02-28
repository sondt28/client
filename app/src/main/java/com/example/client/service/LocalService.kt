package com.example.client.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.client.ui.getallstudent.StudentPagingSource
import com.example.common.model.StudentSimple
import com.example.common.model.Student
import com.example.common.model.Subject
import com.example.database.IStudentAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocalService : Service() {
    private val binder = LocalBinder()

    private val scopeMain = CoroutineScope(Dispatchers.Main)
    private val scopeIO = CoroutineScope(Dispatchers.IO)

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    private val _studentPageFlow = MutableStateFlow<Flow<PagingData<StudentSimple>>?>(null)
    val studentPagerFlow = _studentPageFlow.asStateFlow()

    private val _getAllUiState = MutableStateFlow(GetAllUiState())
    val getAllUiState = _getAllUiState.asStateFlow()

    private val _top10BySubjectUiState = MutableStateFlow(
        Top10BySubjectUiState(
            subjects = subjects,
            subjectSelected = subjects.first()
        )
    )
    val top10BySubjectUiState = _top10BySubjectUiState.asStateFlow()

    private val _top10BySumUiState = MutableStateFlow(
        Top10BySumUiState(
            cities = cities,
            selectedCity = cities.first(),
            sumOptions = sumOptions,
            sumOptionSelected = sumOptions.first()
        )
    )
    val top10BySumUiState = _top10BySumUiState.asStateFlow()

    private val _searchUiState =
        MutableStateFlow(SearchUiState(cities = cities, selectedCity = cities.first()))
    val searchUiState = _searchUiState.asStateFlow()

    private lateinit var pagingSource: StudentPagingSource

    private var databaseService: IStudentAPI? = null
    private val databaseConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            databaseService = IStudentAPI.Stub.asInterface(p1)
            initDBIfNeed()
            pagingSource = StudentPagingSource(databaseService!!, 10)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            databaseService = null
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): LocalService = this@LocalService
    }

    override fun onBind(intent: Intent): IBinder {
        bindDatabaseService()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        unbindDatabaseService()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        scopeMain.cancel()
        super.onDestroy()
    }

    fun initStudentPager(pageSize: Int = 10) {
        val pager = Pager(
            config = PagingConfig(pageSize = pageSize),
            pagingSourceFactory = {
                pagingSource
            }).flow.cachedIn(scopeIO)

        _studentPageFlow.value = pager

        _getAllUiState.update {
            it.copy(isStartGetPaging = true, offset = pageSize + it.offset)
        }
    }

    private fun initDBIfNeed() {
        scopeMain.launch {
            _homeUiState.update { it.copy(isLoading = true) }

            withContext(Dispatchers.IO) {
                if (!isDBInitialized()) {
                    databaseService?.initDB()
                }
            }

            _homeUiState.update { it.copy(isLoading = false) }
        }
    }

    fun getStudentByFirstNameAndCity(firstName: String, city: String) {
        scopeMain.launch {
            _searchUiState.update { it.copy(isLoading = true, firstName = firstName) }

            val student = withContext(Dispatchers.IO) {
                databaseService?.getStudentByFirstNameAndCity(firstName, city)
            }

            _searchUiState.update { it.copy(isLoading = false, students = student) }
        }
    }

    fun getTop10BySubject(subject: String) {
        scopeMain.launch {
            _top10BySubjectUiState.update { it.copy(isLoading = true, subjectSelected = subject) }

            val students = withContext(Dispatchers.IO) {
                databaseService?.getTop10StudentBySubject(subject)
            }

            _top10BySubjectUiState.update { it.copy(students = students, isLoading = false) }
        }
    }


    fun getStudentsWithPaging(limit: Int, offset: Int, isFrom: String = IS_FROM_LOCAL) {
        scopeMain.launch {
            _getAllUiState.update { it.copy(isLoadingGetStudents = true) }

            val students = withContext(Dispatchers.IO) {
                databaseService?.getStudentsWithPaging(limit, offset)
            }

            _getAllUiState.update {
                it.copy(
                    isLoadingGetStudents = false,
                    students = students,
                    offset = it.offset + 10,
                )
            }
        }
    }

    fun getMoreStudentsPage() {
        scopeMain.launch {
            _getAllUiState.update { it.copy(isLoadingMorePage = true) }

            val students = withContext(Dispatchers.IO) {
                databaseService?.getStudentsWithPaging(
                    _getAllUiState.value.limit,
                    _getAllUiState.value.offset
                )
            }

            _getAllUiState.update {
                it.copy(
                    isLoadingMorePage = false,
                    students = students,
                    offset = it.offset + 10,
                )
            }
        }
    }

    fun getSubjectByStudentId(studentId: Long) {
        scopeMain.launch {
            _getAllUiState.update { state ->
                state.copy(loadingItems = state.loadingItems + studentId)
            }

            val subjects = withContext(Dispatchers.IO) {
                databaseService?.getSubjectByStudentId(studentId)
            }

            _getAllUiState.update { state ->
                state.copy(
                    expandedItems = state.expandedItems + studentId,
                    loadingItems = state.loadingItems - studentId
                )
            }
        }
    }

    fun getTop10BySum(sumOption: String, city: String) {
        scopeMain.launch {
            _top10BySumUiState.update {
                it.copy(
                    isLoading = true,
                    sumOptionSelected = sumOption,
                    selectedCity = city
                )
            }

            val students = withContext(Dispatchers.IO) {
                if (sumOption == "SumA") {
                    databaseService?.getTop10StudentSumAByCity(city)
                } else {
                    databaseService?.getTop10StudentSumBByCity(city)
                }
            }

            _top10BySumUiState.update { it.copy(isLoading = false, students = students) }
        }
    }

    fun toggleExpand(studentId: Long) {
        if (studentId in _getAllUiState.value.expandedItems) {
            _getAllUiState.update { state ->
                state.copy(expandedItems = state.expandedItems - studentId)
            }
        } else {
            _getAllUiState.update { state ->
                state.copy(loadingItems = state.loadingItems + studentId)
            }

            scopeMain.launch {
                try {
                    val subjects = withContext(Dispatchers.IO) {
                        databaseService?.getSubjectByStudentId(studentId)
                    }
                    _getAllUiState.update { state ->
                        state.copy(
                            expandedItems = state.expandedItems + studentId,
                            subjects = state.subjects + (studentId to (subjects ?: emptyList())),
                            loadingItems = state.loadingItems - studentId
                        )
                    }
                } catch (e: Exception) {
                    _getAllUiState.update { state ->
                        state.copy(loadingItems = state.loadingItems - studentId)
                    }
                }
            }
        }
    }

    private fun isDBInitialized(): Boolean {
        return databaseService!!.isDBInitialized()
    }

    private fun bindDatabaseService() {
        Intent(INTENT_DATABASE_SERVICE).also {
            it.setPackage(IStudentAPI::class.java.`package`?.name)
            this.bindService(it, databaseConnection, BIND_AUTO_CREATE)
        }
    }

    private fun unbindDatabaseService() {
        this.unbindService(databaseConnection)
    }

    fun updateSelectedSubject(subjectSelected: String) {
        _top10BySubjectUiState.update { currentState ->
            currentState.copy(subjectSelected = subjectSelected)
        }
    }

    fun updateSelectedCity(selectedCity: String) {
        _top10BySumUiState.update { currentState ->
            currentState.copy(selectedCity = selectedCity)
        }
    }

    fun updateSelectedCitySearch(selectedCity: String) {
        _searchUiState.update { currentState ->
            currentState.copy(selectedCity = selectedCity)
        }
    }

    fun updateSelectedSumOption(sumOptionSelected: String) {
        _top10BySumUiState.update { currentState ->
            currentState.copy(sumOptionSelected = sumOptionSelected)
        }
    }

    fun updateFirstName(firstName: String) {
        _searchUiState.update { currentState ->
            currentState.copy(firstName = firstName)
        }
    }

    companion object {
        private const val INTENT_DATABASE_SERVICE = "istudentapi"
        const val IS_FROM_LOCAL = "local"
        const val IS_FROM_REMOTE = "remote"
    }
}

data class GetAllUiState(
    val isLoadingGetStudents: Boolean = false,
    val isLoadingMorePage: Boolean = false,
    val students: List<StudentSimple>? = null,
    val canPaginate: Boolean = false,
    val offset: Int = 0,
    val limit: Int = 10,
    val isStartGetPaging: Boolean = false,
    val expandedItems: Set<Long> = emptySet(),
    val loadingItems: Set<Long> = emptySet(),
    val subjects: Map<Long, List<Subject>> = emptyMap()
)

data class SearchUiState(
    val firstName: String = "",
    val isLoading: Boolean = false,
    val students: Student? = null,
    val cities: List<String>,
    val selectedCity: String
)

data class Top10BySumUiState(
    val isLoading: Boolean = false,
    val students: List<Student>? = null,
    val cities: List<String>,
    val selectedCity: String,
    val sumOptionSelected: String,
    val sumOptions: List<String>
)

data class Top10BySubjectUiState(
    val isLoading: Boolean = false,
    val students: List<Student>? = null,
    val subjectSelected: String,
    val subjects: List<String>
)

data class HomeUiState(val isLoading: Boolean = false)

private val sumOptions = listOf("SumA", "SumB")

private val cities = listOf(
    "Can Tho", "Da Lat", "Da Nang", "HCM", "Hai Phong",
    "Hanoi", "Hue", "Nha Trang", "Quang Ninh", "Vung Tau"
)

private val subjects = listOf(
    "Math",
    "Physics",
    "Chemistry",
    "Biology",
    "English",
    "Literature",
    "Geography",
    "History",
    "Physical Education",
    "Music"
)
