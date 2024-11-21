package org.iesharia.fabioroom

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.iesharia.fabioroom.data.AppDatabase
import org.iesharia.fabioroom.data.Task
import org.iesharia.fabioroom.data.TaskDao
import org.iesharia.fabioroom.data.TiposTareas

@Composable
fun TaskApp(database: AppDatabase) {
    val taskDao = database.taskDao()
    val tiposTareasDao = database.tiposTareasDao()
    val scope = rememberCoroutineScope()

    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var newTaskName by remember { mutableStateOf("") }

    // Cargar tareas al iniciar
    LaunchedEffect(Unit) {
        tasks = taskDao.getAllTasks()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo de texto para agregar una nueva tarea
        OutlinedTextField(
            value = newTaskName,
            onValueChange = { newTaskName = it },
            label = { androidx.compose.material.Text("New Task") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón para agregar tarea
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    // Verificar si el tipo de tarea existe, de lo contrario agregarlo
                    val tipoTarea = TiposTareas(id = 2, titulo = "Tipo de tarea 2")
                    tiposTareasDao.insert(tipoTarea)

                    // Obtener el id del tipo de tarea recién insertado
                    val tipoTareaId = tiposTareasDao.getAllTiposTareas().lastOrNull()?.id ?: 1

                    // Crear y agregar una nueva tarea
                    val newTask = Task(id = 0, titulo = newTaskName, id_tipostareas = tipoTareaId)
                    taskDao.insert(newTask)

                    // Actualizar la lista de tareas
                    tasks = taskDao.getAllTasks()
                    newTaskName = "" // Limpiar el campo
                }
            }
        ) {
            androidx.compose.material.Text("Add Task")
        }

        // Mostrar lista de tareas
        tasks.forEach { task ->
            androidx.compose.material.Text(text = task.titulo)
        }
    }
}
