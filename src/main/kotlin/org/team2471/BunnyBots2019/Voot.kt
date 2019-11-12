package org.team2471.BunnyBots2019

import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.*
import org.team2471.BunnyBots2019.Solenoids.EXTENDER
import org.team2471.BunnyBots2019.Solenoids.LOCK_INNER
import org.team2471.BunnyBots2019.Talons.ELEVATOR_MASTER
import org.team2471.BunnyBots2019.Talons.ELEVATOR_SLAVE
import org.team2471.BunnyBots2019.Talons.VACUUM_MOTOR
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.units.*


object Voot : Subsystem("Voot") {

    val elevatorMotors = MotorController(TalonID(ELEVATOR_MASTER), TalonID(ELEVATOR_SLAVE)).config {
        brakeMode()
//        encoderType(FeedbackDevice.Analog)
    }

    // TODO: need vacuum pressure sensor
    val vacuumMotor = MotorController(TalonID(VACUUM_MOTOR)).config {
       // encoderType(FeedbackDevice.Analog)
    }
//
//   private val gearShifter = Solenoid(SHIFTER)

    private val extenderSolenoid = Solenoid(EXTENDER)

    private val lockInSolenoid = Solenoid(LOCK_INNER)

    private val analogSensor = AnalogInput(AnalogSensor.VACUUM_PRESSURE)

    private val table = NetworkTableInstance.getDefault().getTable(name)

    var isClimbing = false // TODO: isClimbing is false!
//        set(value) {
//            if (value != isClimbing) elevatorMotors.config(0) {
//                pid {
//                    if (value) {
//                        motionMagic(ELEVATOR_CLIMB_ACCELERATION, ELEVATOR_CLIMB_VELOCITY)
//                    } else {
//                        motionMagic(ELEVATOR_ACCELERATION, ELEVATOR_VELOCITY)
//                    }
//                }
//            }
//            gearShifter.set(value)
//            field = value
//        }

//    val heightRange: DoubleRange
//        get() = if (!isClimbing) min(
//            Pose.CARGO_GROUND_PICKUP.elevatorHeight.asInches,
//            height.asInches
//        )..0.0 else Pose.LIFTED.elevatorHeight.asInches..0// inches // TODO: Elevator height?

    val height: Length
        get() = elevatorMotors.position.inches

    val vacuumPressure: Int
        get() = analogSensor.averageValue

//    var isSucking: Boolean
//        get() = vacuumSolenoid.get()
//        set(value) {
//            vacuumSolenoid.set(value)
//        }

    var isExtending: Boolean
        get() = extenderSolenoid.get()
        set(value) {
            extenderSolenoid.set(value)
        }

    var isLocked: Boolean
        get() = lockInSolenoid.get()
        set(value) {
          lockInSolenoid.set(value)
        }


    fun setElevatorPower(power: Double) {
        elevatorMotors.setPercentOutput(power)
        table.getEntry("elevatorPowerset").setDouble(power)
        table.getEntry("elevatorPowerget").getDouble(elevatorMotors.output)
    }
//
//    override suspend fun default() {
//        periodic {
//            if(OI.operatorRightTrigger > 0.1) {
//                raise()
//            }
//        }
//    }

    override fun reset() {
        isClimbing = false
        setElevatorPower(0.0)
    }

//    init {
//
//    }
}
