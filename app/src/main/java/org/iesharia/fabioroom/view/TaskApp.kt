package org.iesharia.fabioroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.iesharia.fabioroom.data.AppDatabase
import org.iesharia.fabioroom.data.Task
import org.iesharia.fabioroom.data.TiposTareas

@Composable
fun TaskApp(database: AppDatabase) {
    val taskDao = database.taskDao()
    val tiposTareasDao = database.tiposTareasDao()
    val scope = rememberCoroutineScope()

    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var tipos_tareas by remember { mutableStateOf(listOf<TiposTareas>()) }
    var newTaskName by remember { mutableStateOf("") }
    var newTaskDescription by remember { mutableStateOf("") }
    var selectedTaskType by remember { mutableStateOf<TiposTareas?>(null) }
    var newTypeTaskName by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var editingTask by remember { mutableStateOf<Task?>(null) }
    var editingTaskName by remember { mutableStateOf("") }
    var editingTaskDescription by remember { mutableStateOf("") }

    var editingTipoTarea by remember { mutableStateOf<TiposTareas?>(null) }
    var editingTipoTareaName by remember { mutableStateOf("") }

    // Cargar tareas y tipos de tareas al iniciar
    LaunchedEffect(Unit) {
        tasks = taskDao.getAllTasks()
        tipos_tareas = tiposTareasDao.getAllTiposTareas()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFADD8E6))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Campo de texto para agregar o editar un tipo de tarea
        OutlinedTextField(
            value = if (editingTipoTarea != null) editingTipoTareaName else newTypeTaskName,
            onValueChange = {
                if (editingTipoTarea != null) editingTipoTareaName = it else newTypeTaskName = it
            },
            label = { Text(if (editingTipoTarea != null) "Editar tipo de tarea" else "Nombre del tipo") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón para guardar o agregar tipo de tarea
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    if (editingTipoTarea != null) {
                        editingTipoTarea?.let {
                            it.titulo = editingTipoTareaName
                            tiposTareasDao.update(it)
                        }
                        editingTipoTarea = null
                        editingTipoTareaName = ""
                    } else if (newTypeTaskName.isNotEmpty()) {
                        val newTypeTask = TiposTareas(titulo = newTypeTaskName)
                        tiposTareasDao.insert(newTypeTask)
                        newTypeTaskName = ""
                    }
                    tipos_tareas = tiposTareasDao.getAllTiposTareas()
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(if (editingTipoTarea != null) "Guardar cambios" else "Agregar tipo de tarea")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campos para agregar o editar una tarea
        OutlinedTextField(
            value = if (editingTask != null) editingTaskName else newTaskName,
            onValueChange = {
                if (editingTask != null) editingTaskName = it else newTaskName = it
            },
            label = { Text(if (editingTask != null) "Editar tarea" else "Nombre de la tarea") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = if (editingTask != null) editingTaskDescription else newTaskDescription,
            onValueChange = {
                if (editingTask != null) editingTaskDescription = it else newTaskDescription = it
            },
            label = { Text(if (editingTask != null) "Editar descripción" else "Descripción de la tarea") },
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

        // Botón para guardar o agregar tarea
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    if (editingTask != null) {
                        editingTask?.let {
                            it.titulo = editingTaskName
                            it.descripcion = editingTaskDescription
                            it.id_tipostareas = selectedTaskType?.id ?: it.id_tipostareas
                            taskDao.update(it)
                        }
                        editingTask = null
                        editingTaskName = ""
                        editingTaskDescription = ""
                        selectedTaskType = null
                    } else if (newTaskName.isNotEmpty() && selectedTaskType != null) {
                        val newTask = Task(
                            id = 0,
                            titulo = newTaskName,
                            descripcion = newTaskDescription,
                            id_tipostareas = selectedTaskType!!.id
                        )
                        taskDao.insert(newTask)
                        newTaskName = ""
                        newTaskDescription = ""
                        selectedTaskType = null
                    }
                    tasks = taskDao.getAllTasks()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = (editingTask != null || (selectedTaskType != null && newTaskName.isNotEmpty() && newTaskDescription.isNotEmpty()))
        ) {
            Text(if (editingTask != null) "Guardar cambios" else "Agregar tarea")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar lista de tipos de tarea
        Text("Tipos de tarea:", style = MaterialTheme.typography.h6)
        tipos_tareas.forEach { tipo ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = tipo.titulo, style = MaterialTheme.typography.h6)
                    Text(text = "ID: ${tipo.id}", style = MaterialTheme.typography.body1)
                }
                Row {
                    Button(onClick = {
                        editingTipoTarea = tipo
                        editingTipoTareaName = tipo.titulo
                    }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        scope.launch(Dispatchers.IO) {
                            tiposTareasDao.delete(tipo)
                            tipos_tareas = tiposTareasDao.getAllTiposTareas()
                            tasks = taskDao.getAllTasks()
                        }
                    }) { Icon(Icons.Default.Close, contentDescription = "Eliminar") }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar lista de tareas
        Text("Tareas:", style = MaterialTheme.typography.h6)
        tasks.forEach { task ->
            val tipoTareaTitulo = tipos_tareas.find { it.id == task.id_tipostareas }?.titulo ?: "Desconocido"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(text = " ${task.titulo}", style = MaterialTheme.typography.h6)
                        Text(
                            text = " ${task.descripcion}",
                            style = MaterialTheme.typography.body1,
                            color = Color.Gray
                        )
                        Text(text = " $tipoTareaTitulo", style = MaterialTheme.typography.body2)
                    }
                }
                    Row {
                        Button(onClick = {
                            editingTask = task
                            editingTaskName = task.titulo
                            editingTaskDescription = task.descripcion
                            selectedTaskType = tipos_tareas.find { it.id == task.id_tipostareas }
                        }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            scope.launch(Dispatchers.IO) {
                                taskDao.delete(task)
                                tasks = taskDao.getAllTasks()
                            }
                        }) { Icon(Icons.Default.Close, contentDescription = "Eliminar") }
                    }
                }
        }
    }
}
