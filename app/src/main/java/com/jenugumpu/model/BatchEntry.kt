package com.jenugumpu.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batch_entries")
data class BatchEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val batchId: String,         // e.g., "JG-2024-001"
    val grade: String,           // A, B, C
    val floralSource: String,
    val totalQuantityKg: Double,
    val jarCount: Int,
    val jarSizeML: Int,          // 250, 500, 1000
    val harvestDate: Long,
    val packagingDate: Long,
    val location: String,
    val harvesterName: String,
    val status: String = "PACKED", // PACKED, SOLD, DELIVERED
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class BatchStatus(val kannadaLabel: String) {
    PACKED("ಪ್ಯಾಕ್ ಮಾಡಲಾಗಿದೆ"),
    SOLD("ಮಾರಾಟವಾಗಿದೆ"),
    DELIVERED("ತಲುಪಿಸಲಾಗಿದೆ")
}
