package org.team2471.BunnyBots2019

import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees

data class Pose(val shoulderAngle: Angle, val wristAngle: Angle) {
    companion object {
        val START_POSE = Pose((-53).degrees, 48.degrees)
        val SCORING_POSE = Pose((-85).degrees, 50.degrees)
        val SAFETY_POSE = Pose((-13).degrees, 175.degrees)
        val GROUND_POSE = Pose((-3).degrees, 59.degrees)
        val STEAL_POSE = Pose(90.degrees, 41.degrees)
        //val ARM_OUT_POSE = Pose((-90).degrees, (-90).degrees)
    }
}