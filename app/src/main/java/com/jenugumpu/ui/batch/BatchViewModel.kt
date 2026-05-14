package com.jenugumpu.ui.batch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.jenugumpu.data.database.AppDatabase
import com.jenugumpu.data.repository.BatchRepository
import com.jenugumpu.model.BatchEntry
import kotlinx.coroutines.launch
import java.util.Calendar

class BatchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BatchRepository
    val allBatches: LiveData<List<BatchEntry>>
    val totalBatchCount: LiveData<Int>

    init {
        val dao = AppDatabase.getDatabase(application).batchDao()
        repository = BatchRepository(dao)
        allBatches      = repository.allBatches
        totalBatchCount = repository.totalBatchCount
    }

    fun insertBatch(batch: BatchEntry) {
        viewModelScope.launch { repository.insert(batch) }
    }

    fun updateBatchStatus(batchId: String, status: String) {
        viewModelScope.launch { repository.updateStatus(batchId, status) }
    }

    fun deleteBatch(batch: BatchEntry) {
        viewModelScope.launch { repository.delete(batch) }
    }

    suspend fun generateBatchId(): String {
        val lastId = repository.getLastId() ?: 0L
        val nextNum = (lastId + 1).toString().padStart(3, '0')
        val year = Calendar.getInstance().get(Calendar.YEAR)
        return "JG-$year-$nextNum"
    }
}
