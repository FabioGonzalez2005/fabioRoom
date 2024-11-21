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
    var tipos_tareas by remember { mutableStateOf(listOf<TiposTareas>()) }
    var newTaskName by remember { mutableStateOf("") }
    var newTaskTypeId by remember { mutableStateOf("") }
    var newTypeTaskName by remember { mutableStateOf("") }

    // Cargar tareas y tipos de tareas al iniciar
    LaunchedEffect(Unit) {
        tasks = taskDao.getAllTasks()
        tipos_tareas = tiposTareasDao.getAllTiposTareas()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo de texto para agregar un nuevo tipo de tarea
        OutlinedTextField(
            value = newTypeTaskName,
            onValueChange = { newTypeTaskName = it },
            label = { Text("Nombre del tipo") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón para agregar tipo de tarea
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    if (newTypeTaskName.isNotEmpty()) {
                        val newTypeTask = TiposTareas(titulo = newTypeTaskName)
                        tiposTareasDao.insert(newTypeTask)
                        tipos_tareas = tiposTareasDao.getAllTiposTareas()
                        newTypeTaskName = ""
                    }
                }
            }
        ) {
            Text("Agregar tipo de tarea")
        }

        // Campos de texto para agregar una nueva tarea
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = newTaskName,
                onValueChange = { newTaskName = it },
                label = { Text("Nombre de la tarea") },
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = newTaskTypeId,
                onValueChange = { newTaskTypeId = it },
                label = { Text("ID del tipo") },
                modifier = Modifier.weight(1f)
            )
        }

        // Botón para agregar tarea
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val tipoTareaId = newTaskTypeId.toIntOrNull()
                    if (!newTaskName.isNullOrEmpty() && tipoTareaId != null) {
                        val newTask = Task(id = 0, titulo = newTaskName, id_tipostareas = tipoTareaId)
                        taskDao.insert(newTask)
                        tasks = taskDao.getAllTasks()
                        newTaskName = ""
                        newTaskTypeId = ""
                    }
                }
            }
        ) {
            Text("Agregar tarea")
        }

        // Mostrar lista de tipos de tarea
        tipos_tareas.forEach { tipo ->
            Column {
                Text(text = "Tipo: ${tipo.id}, Nombre: ${tipo.titulo}")
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            tiposTareasDao.delete(tipo)
                            tipos_tareas = tiposTareasDao.getAllTiposTareas()
                        }
                    }
                ) {
                    Text("Eliminar tipo")
                }
            }
        }

        // Mostrar lista de tareas con sus tipos de tareas
        tasks.forEach { task ->
            Column {
                val tipoTareaTitulo = tipos_tareas.find { it.id == task.id_tipostareas }?.titulo ?: "Desconocido"
                Text(text = "Tarea: ${task.titulo}, ID: ${task.id}, Tipo: $tipoTareaTitulo")
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            taskDao.delete(task)
                            tasks = taskDao.getAllTasks()
                        }
                    }
                ) {
                    Text("Eliminar tarea")
                }
            }
        }
    }
}



