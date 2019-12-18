package org.team2471.BunnyBots2019

import edu.wpi.first.wpilibj.Timer
import org.team2471.BunnyBots2019.Sparks.CUBE_INTAKE
import org.team2471.BunnyBots2019.Sparks.DRIVE_BACKLEFT
import org.team2471.BunnyBots2019.Sparks.DRIVE_BACKRIGHT
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees
import kotlin.math.absoluteValue

object Slurpy : Subsystem("Slurpy") {
    const val SHOULDER_OFFSET = -53.0
    const val WRIST_OFFSET = 48.0
    val cubeIntakeMotor = MotorController(SparkMaxID(Sparks.CUBE_INTAKE))
    val shoulderMotor = MotorController(SparkMaxID(Sparks.ARM_MOVEMENT)).config(20) {
        feedbackCoefficient = 90.0 / 1014.0
        //setRawOffsetConfig(analogAngle)
        inverted(false)
        setSensorPhase(false)
        pid {
            p(0.00000375) //0.000075
            d(0.0) //0.00025
        }
    }

    val wristMotor = MotorController(SparkMaxID(Sparks.WRIST_MOVEMENT)).config(20) {
        feedbackCoefficient = 90.0 / 1014.0
        //setRawOffsetConfig(analogAngle)
        inverted(true)
        setSensorPhase(false)
        pid {
            p(0.00000375) //0.000075
            d(0.0) //0.00025
        }
    }

    var shoulderAngle: Angle
        get() = shoulderMotor.position.degrees + SHOULDER_OFFSET.degrees
        set(value) = shoulderMotor.setPositionSetpoint(value.asDegrees)

    var wristAngle: Angle
        get() = wristMotor.position.degrees + WRIST_OFFSET.degrees
        set(value) = wristMotor.setPositionSetpoint(value.asDegrees)

    val shoulderAngleError: Angle
        get() = shoulderMotor.closedLoopError.degrees

    val wristAngleError: Angle
        get() = wristMotor.closedLoopError.degrees



    fun resetShoulderAngle() {
        shoulderMotor.position = 0.0
    }

    fun resetWristAngle(){
        wristMotor.position = 0.0
    }

    override fun reset() {

    }


}

suspend fun goToPose(targetPose: Pose)  {
    val timer = Timer().apply { start() }

    periodic {
        Slurpy.shoulderMotor.setPositionSetpoint(targetPose.shoulderAngle.asDegrees)
        Slurpy.wristMotor.setPositionSetpoint(targetPose.wristAngle.asDegrees)
        val shoulderError = Slurpy.shoulderAngleError
        val wristError = Slurpy.wristAngleError
        if (shoulderError.asDegrees.absoluteValue < 10.0 &&
            wristError.asDegrees.absoluteValue < 10.0 &&
            timer.get() > 0.3
        ) {
            stop()
        }
    }
}

suspend fun animateToPose(pose: Pose, time: Double = 1.5)= use(Slurpy) {
    val shoulderCurve = MotionCurve()
    val wristCurve = MotionCurve()

    shoulderCurve.storeValue(0.0, Slurpy.shoulderAngle.asDegrees)
    wristCurve.storeValue(0.0, Slurpy.wristAngle.asDegrees)

    shoulderCurve.storeValue(time, pose.shoulderAngle.asDegrees)
    wristCurve.storeValue(time, pose.wristAngle.asDegrees)

    val timer = Timer()
    timer.start()

    periodic {
        val t = timer.get()

        Slurpy.shoulderAngle = shoulderCurve.getValue(t).degrees
        Slurpy.wristAngle = wristCurve.getValue(t).degrees

        if(t > time) {
            this.stop()
        }
    }
    println("Animation took ${timer.get()} seconds")
}