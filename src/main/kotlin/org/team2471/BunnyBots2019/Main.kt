@file:JvmName("Main")

package org.team2471.BunnyBots2019

import edu.wpi.first.wpilibj.*
//import org.team2471.LilBoi2019Peak.testing.driveTests
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.RobotProgram
import org.team2471.frc.lib.framework.initializeWpilib
import org.team2471.frc.lib.framework.runRobotProgram
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.units.degrees
import org.team2471.frc.lib.units.radians
import java.lang.Math.abs

val PDP = PowerDistributionPanel()

object Robot : RobotProgram {

    init {
        Drive.zeroGyro()
        Drive.heading = 0.0.degrees

        // i heard the first string + double concatenations were expensive...
        repeat(25) {
            println("RANDOM NUMBER: ${Math.random()}")
        }
        println("TAKE ME HOOOOOME COUNTRY ROOOOOOOOADS TOOO THE PLAAAAAAACE WHERE I BELOOOOOOOOONG")
        //I swear there was a good reason for this but i honestly have no idea what that was
    }

    override suspend fun enable() {
        Drive.enable()
        Drive.zeroGyro()
        Charm.enable()
        Limelight.enable()
        //Voot.enable()
        Charm.extending = false
        //Voot.isLocked = false
    }

    override suspend fun autonomous() {
        Drive.zeroGyro()
        AutoChooser.autonomous()
    }

    override suspend fun teleop() {
    }

    override suspend fun test() = use(Charm) {
        //Drive.driveTests()
//        Drive.steeringTests()
//        Voot.vacuumTests()

//        var hatchMotor = MotorController(TalonID(7))
//        hatchMotor.setPercentOutput(0.5)
//        delay(3.0)
//        hatchMotor.setPercentOutput(0.0)
//        println("testing hatch")
//

//        for (i in 0..7) {
//            var iSolenoid = Solenoid(i)
//            iSolenoid.set(true)
//            delay(3.0)
//            iSolenoid.set(false)
//            println("Hatch extender test $i. Hi.")
//            delay(1.0)
//        }

    //Drive.tuneDrivePositionController(OI.driverController)

        Charm.ballMotor.setRawOffsetBasedOnAnalog()
        var angle= Charm.ballMotor.position.degrees

       // OI.driverController::a.whenTrue { angle += 90.0 }

        periodic {
            val encoder = Charm.ballMotor.position.degrees
            angle = encoder + (OI.driveTranslation.angle.radians - encoder).wrap()
            val err = Charm.ballMotor.position - angle.asDegrees
            if (abs(err) < 0.75) {
                Charm.ballMotor.setPercentOutput(0.0)
            } else {
                Charm.ballMotor.setPositionSetpoint(angle.asDegrees)
            }

            println("Incremental Angle=${Charm.ballMotor.position} Set Point=$angle") //Analog Angle=${Charm.ballMotor.analogAngle}
        }
    }

    override suspend fun disable() {
        Charm.extending = false
        Drive.disable()
        Charm.disable()
        //Voot.disable()
        Limelight.disable()
    }
}

fun main() {
    initializeWpilib()
    Drive
    Charm
    OI
    AutoChooser

    runRobotProgram(Robot)
}