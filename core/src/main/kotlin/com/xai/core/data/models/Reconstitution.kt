package com.xai.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "reconstitutions")
data class Reconstitution(
    @PrimaryKey val medId: Long = 0,
    val powderAmount: Double = 0.0,
    val solventVolume: Double = 0.0,
    val desiredConcentration: Double? = null,
    val calculatedVolumePerDose: Double? = null
)