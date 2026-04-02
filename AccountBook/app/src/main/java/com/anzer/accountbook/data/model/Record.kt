package com.anzer.accountbook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: Int,
    val amount: Double,
    val category: String,
    val note: String = "",
    val date: Long = System.currentTimeMillis()
)
