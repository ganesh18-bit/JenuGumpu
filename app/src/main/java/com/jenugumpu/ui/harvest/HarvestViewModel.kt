package com.jenugumpu.ui.harvest

import android.app.Application
import androidx.lifecycle.*
import com.jenugumpu.data.database.AppDatabase
import com.jenugumpu.data.repository.HarvestRepository
import com.jenugumpu.model.HarvestEntry
import kotlinx.coroutines.launch
import java.util.*

class HarvestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HarvestRepository
    val allHarvests: LiveData<List<HarvestEntry>>
    val recentHarvests: LiveData<List<HarvestEntry>>
    val totalStock: LiveData<Double?>
    val memberCount: LiveData<Int>
    val totalEntries: LiveData<Int>

    // Today's harvest as a simple MutableLiveData updated on demand
    private val _todayHarvest = MutableLiveData<Double?>(0.0)
    val todayHarvest: LiveData<Double?> = _todayHarvest

    private val _insertResult = MutableLiveData<Boolean>()
    val insertResult: LiveData<Boolean> = _insertResult

    init {
        val db = AppDatabase.getDatabase(application)
        repository = HarvestRepository(db.harvestDao())
        allHarvests   = repository.allHarvests
        recentHarvests = repository.recentHarvests
        totalStock    = repository.totalStock
        memberCount   = repository.memberCount
        totalEntries  = repository.totalEntries
        refreshTodayHarvest()
    }

    fun refreshTodayHarvest() = viewModelScope.launch {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0)
        val start = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis
        val value = repository.getTodayTotal(start, end)
        _todayHarvest.postValue(value)
    }

    fun insertHarvest(entry: HarvestEntry) = viewModelScope.launch {
        try { repository.insert(entry); _insertResult.postValue(true) }
        catch (e: Exception) { _insertResult.postValue(false) }
    }

    fun updateHarvest(entry: HarvestEntry) = viewModelScope.launch { repository.update(entry) }
    fun deleteHarvest(entry: HarvestEntry) = viewModelScope.launch { repository.delete(entry) }
    fun deleteById(id: Long)               = viewModelScope.launch { repository.deleteById(id) }

    fun getHarvestsBySource(source: String) = repository.getHarvestsBySource(source)
    fun getHarvestsByGrade(grade: String)   = repository.getHarvestsByGrade(grade)
}
