package com.xai.dosify.feature.advanced.utils

object ReconstitutionUtils {
    fun calculateConcentration(powderAmount: Double, solventVolume: Double): Double {
        return powderAmount / solventVolume
    }

    fun calculateVolumePerDose(desiredDose: Double, concentration: Double): Double {
        return desiredDose / concentration
    }
}