package com.jenugumpu

import android.app.Application
import com.jenugumpu.data.database.AppDatabase

class JenuGumpuApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}
