package com.example.client.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.example.client.ui.getallstudent.PaginationState
import com.example.common.model.Student
import com.example.common.model.StudentSimple
import com.example.common.model.Subject
import com.example.database.IStudentAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocalService : Service() {
    private val binder = LocalBinder()

    private val scopeMain = CoroutineScope(Dispatchers.Main)

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

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

    private var databaseService: IStudentAPI? = null
    private val databaseConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            databaseService = IStudentAPI.Stub.asInterface(p1)
            initDBIfNeed()
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

    fun getStudentsWithPaging(limit: Int = 10, page: Int) {
        updateInitialPaginationState()
        scopeMain.launch {
            val students = fetchStudentWithPaging(limit, page) ?: emptyList()
            val canPaginate = students.size == limit
            updateStudentList(students, canPaginate)
        }
    }

    suspend fun fetchStudentWithPaging(limit: Int = 10, page: Int): List<StudentSimple>? {
        return withContext(Dispatchers.IO) {
            databaseService?.getStudentsWithPaging(limit, page * limit)
        }
    }

    fun updateInitialPaginationState() {
        _getAllUiState.update {
            it.copy(
                paginationState = when {
                    it.page == 0 -> PaginationState.FIRST_LOADING
                    it.paginationState == PaginationState.REQUEST_INACTIVE -> PaginationState.PAGINATING
                    else -> it.paginationState
                }
            )
        }
    }

    fun updateStudentList(newStudents: List<StudentSimple>, canPaginate: Boolean) {
        _getAllUiState.update {
            it.copy(
                students = it.students + newStudents,
                paginationState = if (canPaginate) PaginationState.REQUEST_INACTIVE else PaginationState.PAGINATION_EXHAUST,
                page = if (canPaginate) it.page + 1 else it.page,
                canPaginate = canPaginate
            )
        }
    }

    fun clearPaging() {
        _getAllUiState.update {
            it.copy(
                page = 0,
                paginationState = PaginationState.REQUEST_INACTIVE,
                canPaginate = false,
                students = emptyList()
            )
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
    }
}

data class GetAllUiState(
    val students: List<StudentSimple> = emptyList(),
    val expandedItems: Set<Long> = emptySet(),
    val loadingItems: Set<Long> = emptySet(),
    val subjects: Map<Long, List<Subject>> = emptyMap(),
    val paginationState: PaginationState = PaginationState.REQUEST_INACTIVE,
    val canPaginate: Boolean = false, // is more page can available
    val page: Int = 0
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
