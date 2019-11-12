package org.team2471.BunnyBots2019.actions

import org.team2471.BunnyBots2019.OI
import org.team2471.BunnyBots2019.Voot
import org.team2471.BunnyBots2019.Voot.setElevatorPower
import org.team2471.BunnyBots2019.Voot.vacuumMotor
import org.team2471.BunnyBots2019.Voot.vacuumPressure
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.util.Timer

suspend fun suck() = use(Voot) {
    vacuumMotor.setPercentOutput(1.0)
    setElevatorPower(-0.15)
    periodic {
        if(vacuumPressure < 2065) {
        } else {
            setElevatorPower(0.0)
            OI.driverController.rumble = 0.25
            this.stop()
        }
    }
}

suspend fun raise() = use(Voot) {
    OI.driverController.rumble = 0.0
    val timer = Timer() //used 2471 frc one
    timer.start()
    var startTime = timer.get()
    // detect elevator at max height for > .25 s
    // then eject locking solenoid
    periodic {
        setElevatorPower(1.0 * OI.operatorLeftY)
    }
}



/**
//
//        OI.driverController.rumble = 0.0
//        val timer = Timer() //used 2471 frc one
//        timer.start()
//        var startTime = timer.get()
//        // detect elevator at max height for > .25 s
//        // then eject locking solenoid
//        periodic {
//            var elevatorAmperage = elevatorMotors.getAmperage()
//            if(elevatorAmperage < 42.0) {  //TODO: get back to this
//                setElevatorPower(-0.6)
//                println("Hi. Elevator amperage is < 42. Elevator Amperage: $elevatorAmperage")
//                startTime = timer.get()
//            } else {
//                var timeLength = timer.get() - startTime
//                if(timeLength > 0.25) {
//                    println("Will (hopefully) lock in now. Hi.")
//                    isLocked = true
//                    setElevatorPower(0.0)
//                    this.stop()
//                } else {
//                    println("Elevator amperage > 42 ($elevatorAmperage), but it wasn't for more than 25 seconds. Hi.")
//                }
//            }
        **/











//        if (OI.driverController.rightTrigger > 0.1) {
//            println("Hi. Elevator Motor Amperage: ${elevatorMotors.getAmperage()}")
//            var power = OI.driveRightTrigger * -0.6 - 0.1
//            setElevatorPower(power)
//            println("Elevator test. Hi. Elevator Power: $power")
//            startTime = timer.get()
//        } else {
//             var timeLength = timer.get() - startTime
//            println("Raise has stopped. Hi.")
//            if(timeLength > 0.25) {
//                println("Will (hopefully) eventually lock in now. Hi.")
//            }
//            setElevatorPower(0.0)
//            this.stop()
//        }



/*
 fun lesson()
 */
