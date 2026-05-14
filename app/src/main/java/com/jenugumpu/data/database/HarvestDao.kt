package com.jenugumpu.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jenugumpu.model.HarvestEntry

@Dao
interface HarvestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHarvest(entry: HarvestEntry): Long

    @Update
    suspend fun updateHarvest(entry: HarvestEntry)

    @Delete
    suspend fun deleteHarvest(entry: HarvestEntry)

    @Query("SELECT * FROM harvest_entries ORDER BY date DESC")
    fun getAllHarvests(): LiveData<List<HarvestEntry>>

    @Query("SELECT * FROM harvest_entries ORDER BY date DESC LIMIT 5")
    fun getRecentHarvests(): LiveData<List<HarvestEntry>>

    @Query("SELECT SUM(quantityKg) FROM harvest_entries")
    fun getTotalStock(): LiveData<Double?>

    @Query("SELECT COUNT(*) FROM harvest_entries")
    fun getTotalEntries(): LiveData<Int>

    @Query("SELECT COUNT(DISTINCT harvesterName) FROM harvest_entries")
    fun getMemberCount(): LiveData<Int>

    // Suspend (non-LiveData) version for ViewModel coroutine use
    @Query("SELECT SUM(quantityKg) FROM harvest_entries WHERE date >= :start AND date <= :end")
    suspend fun getTodayHarvestSync(start: Long, end: Long): Double?

    @Query("SELECT * FROM harvest_entries WHERE floralSource = :source ORDER BY date DESC")
    fun getHarvestsBySource(source: String): LiveData<List<HarvestEntry>>

    @Query("SELECT * FROM harvest_entries WHERE grade = :grade ORDER BY date DESC")
    fun getHarvestsByGrade(grade: String): LiveData<List<HarvestEntry>>

    @Query("SELECT SUM(quantityKg) FROM harvest_entries WHERE floralSource = :source")
    suspend fun getTotalBySource(source: String): Double?

    @Query("SELECT SUM(quantityKg) FROM harvest_entries WHERE grade = :grade")
    suspend fun getTotalByGrade(grade: String): Double?

    @Query("DELETE FROM harvest_entries WHERE id = :id")
    suspend fun deleteById(id: Long)
}
