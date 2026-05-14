package com.jenugumpu.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "harvest_entries")
data class HarvestEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val harvesterName: String,
    val date: Long,           // epoch millis
    val location: String,
    val quantityKg: Double,
    val floralSource: String,
    val honeyColor: String,   // LIGHT, AMBER, DARK_AMBER, DARK
    val grade: String,        // A, B, C
    val notes: String = "",
    val batchId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class FloralSource(val kannadaName: String, val emoji: String, val colorHex: String) {
    COFFEE_BLOSSOM("ಕಾಫಿ ಹೂವು", "☕", "#6F4E37"),
    WILDFLOWER("ಕಾಡು ಹೂ", "🌸", "#C084FC"),
    EUCALYPTUS("ನೀಲಗಿರಿ", "🌿", "#34D399"),
    MUSTARD("ಸಾಸಿವೆ", "🌻", "#FACC15"),
    MULTIFLORAL("ಮಿಶ್ರ ಹೂ", "🌺", "#F87171"),
    JAMUN("ನೇರಳೆ", "🫐", "#7C3AED"),
    LITCHI("ಲಿಚಿ", "🍈", "#FB923C")
}

enum class HoneyGrade(val kannadaLabel: String, val description: String, val moisture: String, val priceMultiplier: Double) {
    A("ಗ್ರೇಡ್ A - ಉತ್ತಮ", "ಬಂಗಾರ ಬಣ್ಣ, ಕಡಿಮೆ ತೇವಾಂಶ", "< 17%", 1.0),
    B("ಗ್ರೇಡ್ B - ಮಧ್ಯಮ", "ಅಂಬರ್ ಬಣ್ಣ, ಮಧ್ಯಮ ತೇವಾಂಶ", "17-20%", 0.80),
    C("ಗ್ರೇಡ್ C - ಸಾಮಾನ್ಯ", "ಗಾಢ ಬಣ್ಣ, ಹೆಚ್ಚು ತೇವಾಂಶ", "> 20%", 0.65)
}
