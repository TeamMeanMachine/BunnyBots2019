package org.team2471.BunnyBots2019

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees

object Bintake : Subsystem("Bintake") {

    val pivotMotor = MotorController(TalonID(Talons.BINTAKE_ROTATION)).config {
        encoderType(FeedbackDevice.CTRE_MagEncoder_Absolute)
        encoderContinuous(false)
        // rawOffset(-224)
        sensorPhase(false)
        inverted(false)
        feedbackCoefficient = 1.0 // needs modification
    }

    val intakeMotor = MotorController(TalonID(Talons.BINTAKE_INTAKE)).config {
    }

    var angle: Angle
        get() = pivotMotor.position.degrees
        set(value) = pivotMotor.setPositionSetpoint(value.asDegrees)

    fun intake(power: Double) {
        intakeMotor.setPercentOutput(power)
        println(power)
    }

    fun pivot(power: Double) {
        pivotMotor.setPercentOutput(power)
    }

    override fun reset() {

    }

}
