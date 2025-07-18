package com.xai.dosify.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "supplies")
data class Supply(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val unit: String,
    val stock: Double,
    val lowStockThreshold: Double,
    val linkedMedId: Long? = null
)