package org.team2471.BunnyBots2019

import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees

data class SlurpyPose(val shoulderAngle: Angle, val wristAngle: Angle) {
    companion object {
        val START_POSE = SlurpyPose((-53).degrees, 48.degrees)
        val SCORING_POSE = SlurpyPose((-85).degrees, 65.degrees)
        val SAFETY_POSE = SlurpyPose((-13).degrees, 175.degrees)
        val GROUND_POSE = SlurpyPose((-3).degrees, 59.degrees)
        val STEAL_POSE = SlurpyPose(90.degrees, 41.degrees)
        //val ARM_OUT_POSE = Pose((-90).degrees, (-90).degrees)
    }
}

data class BintakePose(val angle: Angle) {
    companion object {
        val SAFETY_POSE = BintakePose(206.degrees)
        val INTAKE_POSE = BintakePose(150.degrees)
        val SCORING_POSE = BintakePose(40.degrees)
        val SPITTING_POSE = BintakePose(135.degrees)
    }
}