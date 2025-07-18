package com.xai.dosify.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: MedType,
    val strength: Double,
    val unit: String,
    val stock: Double,
    val lowStockThreshold: Double,
    val reconstitution: Boolean = false
)

enum class MedType {
    TABLET, INJECTION, DROPS, OTHER
}