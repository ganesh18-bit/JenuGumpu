package com.jenugumpu.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jenugumpu.model.BatchEntry

@Dao
interface BatchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: BatchEntry): Long

    @Update
    suspend fun updateBatch(batch: BatchEntry)

    @Delete
    suspend fun deleteBatch(batch: BatchEntry)

    @Query("SELECT * FROM batch_entries ORDER BY createdAt DESC")
    fun getAllBatches(): LiveData<List<BatchEntry>>

    @Query("SELECT * FROM batch_entries WHERE batchId = :batchId LIMIT 1")
    suspend fun getBatchById(batchId: String): BatchEntry?

    @Query("SELECT * FROM batch_entries WHERE status = :status ORDER BY createdAt DESC")
    fun getBatchesByStatus(status: String): LiveData<List<BatchEntry>>

    @Query("SELECT COUNT(*) FROM batch_entries")
    fun getTotalBatchCount(): LiveData<Int>

    @Query("SELECT MAX(id) FROM batch_entries")
    suspend fun getLastBatchId(): Long?

    @Query("UPDATE batch_entries SET status = :status WHERE batchId = :batchId")
    suspend fun updateBatchStatus(batchId: String, status: String)
}
