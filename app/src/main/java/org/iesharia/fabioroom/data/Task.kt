package org.iesharia.fabioroom.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = TiposTareas::class,
            parentColumns = ["id"],
            childColumns = ["id_tipostareas"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var descripcion: String,
    var titulo: String,
    var id_tipostareas: Int
)