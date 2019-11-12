package org.team2471.BunnyBots2019.actions

import org.team2471.BunnyBots2019.Charm
import org.team2471.BunnyBots2019.Charm.CARGO_HOLDING_INTAKE_POWER
import org.team2471.BunnyBots2019.Charm.HATCH_HOLDING_INTAKE_POWER
import org.team2471.BunnyBots2019.Charm.HOME_ANGLE
import org.team2471.BunnyBots2019.Charm.angleSetpoint
import org.team2471.BunnyBots2019.Charm.grabber
import org.team2471.BunnyBots2019.Charm.hasCargo
import org.team2471.BunnyBots2019.Charm.hasHatch
import org.team2471.BunnyBots2019.Charm.intake
import org.team2471.BunnyBots2019.Charm.extending
import org.team2471.BunnyBots2019.OI
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.units.Angle
import org.team2471.frc.lib.units.degrees

suspend fun scoreGamePiece(position: Angle) = use(Charm) {
    try{
        var hasSpit = false
        if(hasCargo) {
            intake(CARGO_HOLDING_INTAKE_POWER)
            grabber(-0.4)
            extending = false
            grabber(0.0)
            periodic {
                angleSetpoint = position
                if (OI.driveRightTrigger > 0.1) {
                    hasSpit = true
                    hasCargo = false
                    val cargoPower = OI.driveRightTrigger * -0.6 - 0.4
                    intake(cargoPower)
                } else {
                    intake(CARGO_HOLDING_INTAKE_POWER + OI.operatorLeftTrigger * 0.2)
                }
                if((hasSpit && OI.driveRightTrigger < 0.1) || OI.operatorController.leftBumper) { //changed from 0.5 to match if above
                    this.stop()
                }
            }
        } else {
            extending = false
            intake(-0.3)
            angleSetpoint = HOME_ANGLE.degrees
            grabber(HATCH_HOLDING_INTAKE_POWER)
            periodic {
                if (OI.driveRightTrigger > 0.1) {
                    extending = true
                    hasHatch = false
                    grabber(OI.driveRightTrigger * -0.7 - 0.3)
                    hasSpit = true
                } else {
                    grabber(HATCH_HOLDING_INTAKE_POWER + OI.operatorLeftTrigger * 0.2) //left suck in right spit
                }

                if((hasSpit && OI.driveRightTrigger < 0.1) || OI.operatorController.rightBumper) {
                    this.stop()
                }
            }

        }
    } finally{
        extending = false
        delay(1.0)
        grabber(0.0)
        intake(0.0)
    }
}
