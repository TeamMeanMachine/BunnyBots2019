package org.team2471.BunnyBots2019.actions

import edu.wpi.first.wpilibj.Timer
import org.team2471.BunnyBots2019.Charm
import org.team2471.BunnyBots2019.Charm.CARGO_HOLDING_INTAKE_POWER
import org.team2471.BunnyBots2019.Charm.HATCH_HOLDING_INTAKE_POWER
import org.team2471.BunnyBots2019.Charm.HOME_ANGLE
import org.team2471.BunnyBots2019.Charm.INTAKE_ANGLE
import org.team2471.BunnyBots2019.Charm.angle
import org.team2471.BunnyBots2019.Charm.angleSetpoint
import org.team2471.BunnyBots2019.Charm.grabber
import org.team2471.BunnyBots2019.Charm.hasCargo
import org.team2471.BunnyBots2019.Charm.hasHatch
import org.team2471.BunnyBots2019.Charm.intake
import org.team2471.BunnyBots2019.Charm.extending
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.coroutines.suspendUntil
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.units.degrees
import kotlin.math.abs

suspend fun intakeCargo() = use(Charm) {
    try {
        val timer = Timer()

        //Do checks to make sure hatch grabber is in correct pos
        //move barm down to position
        grabber(-0.4)
        extending = false
        Charm.angleSetpoint = INTAKE_ANGLE.degrees
        grabber(0.0)
        intake(0.75)
        delay(0.25) //Necessary to avoid amperage spike at beginning
//        suspendUntil {
//            println(ballCurrent)
//            ballCurrent > 15.0}

        timer.start()
        var startTime = timer.get()
        periodic {
            if(Charm.ballCurrent < 6.0) {
                startTime = timer.get()
            } else {
                val timeLength = timer.get() - startTime
                if(timeLength > 0.5) {
                    this.stop()
                }
            }
        }
        intake(CARGO_HOLDING_INTAKE_POWER)
        hasCargo = true
        //move everything back to place
        //scoreGamePiece(CARGO_SHIP_ANGLE.degrees)
    }
    finally {
        //idk do stuff
        intake(0.0)
    }
}

suspend fun intakeHatch() = use(Charm) {
    try {
        val timer = Timer()

        intake(-0.3)
        if(abs(HOME_ANGLE - angle.asDegrees) > 20.0) {
            extending = false
        }
        intake(0.0)
        angleSetpoint = HOME_ANGLE.degrees
        delay(0.3)
        extending = true
        grabber(0.4)

        delay(0.25) //Necessary to avoid amperage spike at beginning
        suspendUntil {
            Charm.hatchCurrent > 16.0}
        timer.start()
        val startTime = timer.get()
        suspendUntil {
            println(timer.get() - startTime)
            timer.get() - startTime > 0.3
        }
        hasHatch = true
        extending = false
        delay(0.3)
        //move everything back to place
        //scoreGamePiece(HOME_ANGLE.degrees)
    } finally {
        intake(CARGO_HOLDING_INTAKE_POWER)
        grabber(HATCH_HOLDING_INTAKE_POWER)
        extending = false
    }
}