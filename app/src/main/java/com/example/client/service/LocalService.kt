package com.example.client.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.client.ui.getallstudent.ListState
import com.example.client.ui.getallstudent.StudentPagingSource
import com.example.common.model.Student
import com.example.database.IStudentAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocalService : Service() {
    private val binder = LocalBinder()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

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
        coroutineScope.cancel()
        super.onDestroy()
    }

    private fun initDBIfNeed() {
        coroutineScope.launch {
            _homeUiState.update { it.copy(isLoading = true) }

            withContext(Dispatchers.IO) {
                if (!isDBInitialized()) {
                    databaseService?.initDB()
                }
            }

            _homeUiState.update { it.copy(isLoading = false) }
        }
    }

    fun getStudentPager() {
        val pager = Pager(
            config = PagingConfig(pageSize = 10),
            pagingSourceFactory = { StudentPagingSource(databaseService!!) }
        )
        _getAllUiState.update { it.copy(pager = pager) }
    }

    fun getStudentByFirstNameAndCity(firstName: String, city: String) {
        coroutineScope.launch {
            _searchUiState.update { it.copy(isLoading = true, firstName = firstName) }

            val student = withContext(Dispatchers.IO) {
                databaseService?.getStudentByFirstNameAndCity(firstName, city)
            }

            _searchUiState.update { it.copy(isLoading = false, students = student) }
        }
    }

    fun getTop10BySubject(subject: String) {
        coroutineScope.launch {
            _top10BySubjectUiState.update { it.copy(isLoading = true, subjectSelected = subject) }

            val students = withContext(Dispatchers.IO) {
                databaseService?.getTop10StudentBySubject(subject)
            }

            _top10BySubjectUiState.update { it.copy(students = students, isLoading = false) }
        }
    }


    fun getStudentsWithPaging(limit: Int, offset: Int) {
        coroutineScope.launch {
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
        coroutineScope.launch {
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
        coroutineScope.launch {
            val subjects = withContext(Dispatchers.IO) {
                databaseService?.getSubjectByStudentId(studentId)
            }
        }
    }

    fun getTop10BySum(sumOption: String, city: String) {
        coroutineScope.launch {
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
    val isLoadingGetStudents: Boolean = false,
    val isLoadingMorePage: Boolean = false,
    val students: List<Student>? = null,
    val canPaginate: Boolean = false,
    val offset: Int = 0,
    val limit: Int = 10,
    val listStudentState: ListState = ListState.IDLE,
    val pager: Pager<Int, Student>? = null
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
