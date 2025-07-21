package com.xai.dosify.core.com.xai.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "supplies")
data class Supply(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val unit: String = "",
    val stock: Double = 0.0,
    val lowStockThreshold: Double = 0.0,
    val linkedMedId: Long? = null
)