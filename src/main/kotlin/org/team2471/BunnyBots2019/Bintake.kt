package org.team2471.BunnyBots2019

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees

object Bintake : Subsystem("Bintake") {

    val pivotMotor = MotorController(TalonID(21)).config {
        encoderType(FeedbackDevice.Analog)
        encoderContinuous(false)
        // rawOffset(-224)
        sensorPhase(true)
        feedbackCoefficient = 0.3502 // needs modification
    }

    val intakeMotor = MotorController(TalonID(22)).config {

    }

    val angle: Angle
        get() = pivotMotor.position.degrees

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
