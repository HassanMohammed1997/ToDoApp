package com.semicolon.todoapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import kotlinx.coroutines.flow.Flow

/**
 *Created by Hassan Mohammed on 6/26/21
 */
@Dao
interface TodoDao {
    @Insert(onConflict = IGNORE)
    suspend fun insertTodo(item: TodoEntity)

    @Query("SELECT * FROM todos ORDER BY id ASC")
    fun getTodos(): Flow<List<TodoEntity>>

    @Update
    suspend fun updateTodo(todoEntity: TodoEntity)

    @Delete
    suspend fun deleteTodo(todoEntity: TodoEntity)

    @Query("DELETE FROM todos")
    suspend fun deleteAllTodos()

    @Query("SELECT * FROM todos WHERE title LIKE :query")
    fun search(query: String): LiveData<List<TodoEntity>>

    @Query("SELECT * FROM todos ORDER BY CASE WHEN priority LIKE 'H%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'L%' THEN 3 END")
    fun sortByHighPriority(): LiveData<List<TodoEntity>>

    @Query("SELECT * FROM todos ORDER BY CASE WHEN priority LIKE 'L%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'H%' THEN 3 END")
    fun sortByLowPriority(): LiveData<List<TodoEntity>>

    @Query("SELECT * FROM todos ORDER BY CASE WHEN priority LIKE 'M%' THEN 1 WHEN priority LIKE 'H%' THEN 2 WHEN priority LIKE 'L%' THEN 3 END")
    fun sortByMediumPriority(): LiveData<List<TodoEntity>>

}