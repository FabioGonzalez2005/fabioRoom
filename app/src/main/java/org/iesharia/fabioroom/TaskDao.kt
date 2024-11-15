package org.iesharia.fabioroom

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
