package org.team2471.BunnyBots2019.testing

import org.team2471.BunnyBots2019.Voot
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.coroutines.delay
import org.team2471.frc.lib.framework.use

suspend fun Voot.elevatorTests() = use(this) {
    var iMotor = MotorController(TalonID(6), TalonID(9))
    iMotor.setPercentOutput(-0.1)
    delay(3.0)
    iMotor.setPercentOutput(0.0)
    println("Voot elevator test. Hi.")
}

suspend fun Voot.vacuumTests() = use(this) {
    println("Voot sensor test. Hi. ${Voot.vacuumPressure}")
    var iMotor = MotorController(TalonID(11))
    iMotor.setPercentOutput(1.0)
    delay(4.0)
    println("Voot sensor test. Hi. ${Voot.vacuumPressure}")
    iMotor.setPercentOutput(0.0)
    println("Voot vacuum test. Hi.")

}

//suspend fun Voot.sensorTest() = use(this) {
//    println("Voot sensor test. Hi. $Voot.vacuumPressure")
//}