package org.team2471.BunnyBots2019

import org.team2471.BunnyBots2019.Charm.extending
import org.team2471.BunnyBots2019.Charm.isSetpointLow
import org.team2471.BunnyBots2019.Voot.isExtending
import org.team2471.BunnyBots2019.Voot.isLocked
import org.team2471.BunnyBots2019.actions.*
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.input.*
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.math.cube
import org.team2471.frc.lib.math.deadband
import org.team2471.frc.lib.math.squareWithSign

private val deadBandDriver = 0.1
private val deadBandOperator = 0.1

object OI {
    val driverController = XboxController(0)
    val operatorController = XboxController(1)

    private val driveTranslationX: Double
        get() = driverController.leftThumbstickX.deadband(deadBandDriver).squareWithSign()

    private val driveTranslationY: Double
        get() = -driverController.leftThumbstickY.deadband(deadBandDriver).squareWithSign()

    val driveTranslation: Vector2
        get() = Vector2(driveTranslationX, driveTranslationY) //does owen want this cubed?

    val driveRotation: Double
        get() = (driverController.rightThumbstickX.deadband(deadBandDriver)).cube() * 0.5 //changed from 0.6

    val driveLeftTrigger: Double
        get() = driverController.leftTrigger

    val driveRightTrigger: Double
        get() = driverController.rightTrigger

    val operatorLeftTrigger: Double
        get() = operatorController.leftTrigger

    val operatorLeftY: Double
        get() = operatorController.leftThumbstickY.deadband(0.2)

    val operatorRightTrigger: Double
        get() = operatorController.rightTrigger


    val testArm: Double
        get() = operatorController.rightThumbstickX.deadband(deadBandOperator)

    init {
        driverController::back.whenTrue { Drive.zeroGyro() }
        driverController::x.whenTrue { Charm.extending = !Charm.extending }
        ({ driverController.leftTrigger > 0.1 }).whileTrue {
            extending = true
            visionDrive()
        }
        ({ driverController.dPad == Controller.Direction.DOWN }).toggleWhenTrue { suck() }
        operatorController::y.whenTrue { isSetpointLow = false}
        operatorController::a.whenTrue { isSetpointLow = true }
        operatorController::start.whenTrue {
            isExtending = true
            delay(1.0)
            isExtending = false
        }
        ({ Math.abs(operatorController.leftThumbstickY) > 0.1 }).whileTrue {
            raise()
        }
        operatorController::rightBumper.whenTrue { intakeCargo() }
        operatorController::leftBumper.whenTrue { intakeHatch() }
        operatorController::x.whenTrue { isLocked = !isLocked}

        }
        //({operatorController.rightTrigger > 0.1}).whileTrue{ pullInCargoOrHatch() }
        //({ operatorController.leftTrigger > 0.1}).whileTrue{ pushOutCargoOrHatch() }
        //({ operatorController.rightTrigger <= 0.1 || operatorController.leftTrigger <= 0.1}).whileTrue{
         //   intake(CARGO_HOLDING_INTAKE_POWER)
         //   grabber(HATCH_HOLDING_INTAKE_POWER)
        //}
    }

