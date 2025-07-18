package com.xai.dosify.core.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reconstitutions")
data class Reconstitution(
    @PrimaryKey val medId: Long,
    val powderAmount: Double,
    val solventVolume: Double,
    val desiredConcentration: Double? = null,
    val calculatedVolumePerDose: Double? = null
)