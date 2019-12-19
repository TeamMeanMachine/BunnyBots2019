package org.team2471.BunnyBots2019

import edu.wpi.first.wpilibj.Timer
import org.team2471.BunnyBots2019.Sparks.CUBE_INTAKE
import org.team2471.BunnyBots2019.Sparks.DRIVE_BACKLEFT
import org.team2471.BunnyBots2019.Sparks.DRIVE_BACKRIGHT
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.round
import org.team2471.frc.lib.motion_profiling.MotionCurve
import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees
import kotlin.math.absoluteValue

object Slurpy : Subsystem("Slurpy") {
    const val SHOULDER_OFFSET = -53.0
    const val WRIST_OFFSET = 48.0
    var currentPose = Pose.START_POSE
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
        set(value) = shoulderMotor.setPositionSetpoint(value.asDegrees - SHOULDER_OFFSET)

    var wristAngle: Angle
        get() = wristMotor.position.degrees + WRIST_OFFSET.degrees
        set(value) = wristMotor.setPositionSetpoint(value.asDegrees - WRIST_OFFSET)

    val shoulderAngleError: Angle
        get() = shoulderMotor.closedLoopError.degrees

    val wristAngleError: Angle
        get() = wristMotor.closedLoopError.degrees


    fun resetShoulderAngle() {
        shoulderMotor.position = 0.0
    }

    fun resetWristAngle() {
        wristMotor.position = 0.0
    }

    override fun reset() {

    }

    suspend fun prepareSlurpy() {
        animateToPose(Pose.SCORING_POSE)
        delay(0.5)
        animateToPose(Pose.SAFETY_POSE)
    }


    suspend fun goToPose(targetPose: Pose) {
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

    suspend fun animateToPose(pose: Pose, time: Double = 1.0) = use(Slurpy) {
        currentPose = pose
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

            println(
                "Shoulder Error: ${round(
                    Slurpy.shoulderAngle.asDegrees - shoulderCurve.getValue(t),
                    2
                )} and Wrist Error: ${round(Slurpy.wristAngle.asDegrees - wristCurve.getValue(t), 2)} at time ${round(
                    t,
                    2
                )}"
            )

            Slurpy.shoulderAngle = shoulderCurve.getValue(t).degrees
            Slurpy.wristAngle = wristCurve.getValue(t).degrees

            if (t > time) {
                this.stop()
            }
        }
        println("Animation took ${round(timer.get(), 2)} seconds")
    }

    suspend fun intakeCube() {
        try {
            animateToPose(Pose.SAFETY_POSE)
            animateToPose(Pose.GROUND_POSE)
            val timer = Timer()
            timer.start()

            periodic {
                val t = timer.get()
                if (OI.driverController.rightBumper && t < 7.0) {
                    cubeIntakeMotor.setPercentOutput(0.5)
                } else {
                    cubeIntakeMotor.setPercentOutput(0.0)
                    this.stop()
                }
            }
            animateToPose(Pose.SAFETY_POSE)
            animateToPose(Pose.SCORING_POSE)
            cubeIntakeMotor.setPercentOutput(-0.25)
            delay(3.0)
        } finally {
            cubeIntakeMotor.setPercentOutput(0.0)
            animateToPose(Pose.SAFETY_POSE)
        }
    }
}
