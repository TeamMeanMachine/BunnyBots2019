@file:JvmName("Main")

package org.team2471.BunnyBots2019

import edu.wpi.first.wpilibj.*
import org.team2471.frc.lib.framework.RobotProgram
import org.team2471.frc.lib.framework.initializeWpilib
import org.team2471.frc.lib.framework.runRobotProgram
import org.team2471.frc.lib.units.degrees

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
        Limelight.enable()
    }

    override suspend fun autonomous() {
        Drive.zeroGyro()
        AutoChooser.autonomous()
    }

    override suspend fun teleop() {
    }

    override suspend fun test()  {
        //Drive.driveTests()
//        Drive.steeringTests()
    }

    override suspend fun disable() {
        Drive.disable()
        Limelight.disable()
    }
}

fun main() {
    initializeWpilib()
    Drive
    OI
    AutoChooser

    runRobotProgram(Robot)
}