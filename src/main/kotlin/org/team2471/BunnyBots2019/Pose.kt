package org.team2471.BunnyBots2019

import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees

data class Pose(val shoulderAngle: Angle, val wristAngle: Angle) {
    companion object {
        val START_POSE = Pose((-53).degrees, 48.degrees)
        val SCORING_POSE = Pose((-61).degrees, 54.degrees)
        val SAFETY_POSE = Pose((-7).degrees, 171.degrees)
        val GROUND_POSE = Pose(0.degrees, 60.degrees)
        val STEAL_POSE = Pose(90.degrees, 41.degrees)
    }
}