package com.xai.dosify.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val type: MedType = MedType.OTHER,
    val strength: Double = 0.0,
    val unit: String = "",
    val stock: Double = 0.0,
    val lowStockThreshold: Double = 0.0,
    val reconstitution: Boolean = false
)

enum class MedType {
    TABLET, INJECTION, DROPS, OTHER
}