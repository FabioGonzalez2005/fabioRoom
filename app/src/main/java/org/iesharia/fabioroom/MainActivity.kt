package org.iesharia.fabioroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import org.iesharia.fabioroom.data.AppDatabase
import org.iesharia.fabioroom.view.TaskApp

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar la base de datos
        database = AppDatabase.getDatabase(this)

        setContent {
            TaskApp(database)
        }
    }
}


