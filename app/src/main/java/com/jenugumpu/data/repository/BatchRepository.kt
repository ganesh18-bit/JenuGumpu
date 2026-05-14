package com.jenugumpu.data.repository

import androidx.lifecycle.LiveData
import com.jenugumpu.data.database.BatchDao
import com.jenugumpu.model.BatchEntry

class BatchRepository(private val dao: BatchDao) {

    val allBatches: LiveData<List<BatchEntry>>    = dao.getAllBatches()
    val totalBatchCount: LiveData<Int>            = dao.getTotalBatchCount()

    suspend fun insert(batch: BatchEntry): Long   = dao.insertBatch(batch)
    suspend fun update(batch: BatchEntry)         = dao.updateBatch(batch)
    suspend fun delete(batch: BatchEntry)         = dao.deleteBatch(batch)
    suspend fun updateStatus(id: String, s: String) = dao.updateBatchStatus(id, s)
    suspend fun getLastId(): Long?               = dao.getLastBatchId()
}
