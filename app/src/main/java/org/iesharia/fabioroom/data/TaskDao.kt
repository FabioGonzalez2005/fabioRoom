package org.iesharia.fabioroom.data

import androidx.room.*

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}

@Dao
interface TiposTareasDao {
    @Insert
    suspend fun insert(tipoTarea: TiposTareas)

    @Query("SELECT * FROM tipos_tareas")
    suspend fun getAllTiposTareas(): List<TiposTareas>

    @Update
    suspend fun update(tipoTarea: TiposTareas)

    @Delete
    suspend fun delete(tipoTarea: TiposTareas)
}
