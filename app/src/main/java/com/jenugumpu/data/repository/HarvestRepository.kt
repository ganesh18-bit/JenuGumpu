package com.jenugumpu.data.repository

import androidx.lifecycle.LiveData
import com.jenugumpu.data.database.HarvestDao
import com.jenugumpu.model.HarvestEntry

class HarvestRepository(private val dao: HarvestDao) {

    val allHarvests: LiveData<List<HarvestEntry>>    = dao.getAllHarvests()
    val recentHarvests: LiveData<List<HarvestEntry>> = dao.getRecentHarvests()
    val totalStock: LiveData<Double?>                = dao.getTotalStock()
    val memberCount: LiveData<Int>                   = dao.getMemberCount()
    val totalEntries: LiveData<Int>                  = dao.getTotalEntries()

    fun getHarvestsBySource(source: String) = dao.getHarvestsBySource(source)
    fun getHarvestsByGrade(grade: String)   = dao.getHarvestsByGrade(grade)

    suspend fun insert(entry: HarvestEntry): Long = dao.insertHarvest(entry)
    suspend fun update(entry: HarvestEntry)       = dao.updateHarvest(entry)
    suspend fun delete(entry: HarvestEntry)       = dao.deleteHarvest(entry)
    suspend fun deleteById(id: Long)              = dao.deleteById(id)

    suspend fun getTodayTotal(start: Long, end: Long): Double =
        dao.getTodayHarvestSync(start, end) ?: 0.0

    suspend fun getTotalBySource(source: String): Double = dao.getTotalBySource(source) ?: 0.0
    suspend fun getTotalByGrade(grade: String): Double   = dao.getTotalByGrade(grade) ?: 0.0
}
