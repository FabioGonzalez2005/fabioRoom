package org.iesharia.fabioroom

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
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
    var selectedTaskType by remember { mutableStateOf<TiposTareas?>(null) }
    var newTypeTaskName by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

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
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Agregar tipo de tarea")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campos para agregar una nueva tarea
        OutlinedTextField(
            value = newTaskName,
            onValueChange = { newTaskName = it },
            label = { Text("Nombre de la tarea") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Menú desplegable para seleccionar tipo de tarea
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { dropdownExpanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = selectedTaskType?.titulo ?: "Selecciona un tipo de tarea")
            }
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                tipos_tareas.forEach { tipo ->
                    DropdownMenuItem(
                        onClick = {
                            selectedTaskType = tipo
                            dropdownExpanded = false
                        }
                    ) {
                        Text(text = tipo.titulo)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para agregar tarea
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    if (!newTaskName.isNullOrEmpty() && selectedTaskType != null) {
                        val newTask = Task(id = 0, titulo = newTaskName, id_tipostareas = selectedTaskType!!.id)
                        taskDao.insert(newTask)
                        tasks = taskDao.getAllTasks()
                        newTaskName = ""
                        selectedTaskType = null
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedTaskType != null && newTaskName.isNotEmpty()
        ) {
            Text("Agregar tarea")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar lista de tipos de tarea
        Text("Tipos de tarea:", style = MaterialTheme.typography.h6)
        tipos_tareas.forEach { tipo ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "ID: ${tipo.id}, Nombre: ${tipo.titulo}")
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            tiposTareasDao.delete(tipo)
                            tipos_tareas = tiposTareasDao.getAllTiposTareas()
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar lista de tareas
        Text("Tareas:", style = MaterialTheme.typography.h6)
        tasks.forEach { task ->
            val tipoTareaTitulo = tipos_tareas.find { it.id == task.id_tipostareas }?.titulo ?: "Desconocido"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Tarea: ${task.titulo}, Tipo: $tipoTareaTitulo")
                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            taskDao.delete(task)
                            tasks = taskDao.getAllTasks()
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}