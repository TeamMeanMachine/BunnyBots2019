package org.team2471.BunnyBots2019

// TODO: replace imports
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.BunnyBots2019.OI.driveRightTrigger
import org.team2471.BunnyBots2019.Solenoids.HATCH_EXTENSION
import org.team2471.BunnyBots2019.Sparks.BALL_INTAKE
//import org.team2471.LilBoi2019Peak.Talons.ARM_ANGLE
import org.team2471.frc.lib.actuators.*
import org.team2471.frc.lib.coroutines.MeanlibDispatcher
import org.team2471.frc.lib.coroutines.periodic
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.math.DoubleRange
import org.team2471.frc.lib.units.*

// arm angle = talon; ball intake = spark; hatch intake = victor
object Charm : Subsystem("Charm") {
    const val CARGO_HOLDING_INTAKE_POWER = 0.07
    const val HATCH_HOLDING_INTAKE_POWER = 0.2
    const val HATCH_CURRENT_THRESHOLD = 20  //TODO: measure current threshold

    const val HOME_ANGLE = 85.0
    const val CARGO_SHIP_ANGLE = 76.5
    const val LOW_ROCKET_ANGLE = 50.4
    const val INTAKE_ANGLE = 5.0


    private val hatchMotor = MotorController(TalonID(Talons.HATCH_GRABBER)).config {
        //775
        inverted(true)
    }

    private val armMotor = MotorController(TalonID(Talons.ARM_ANGLE)).config {
        encoderType(FeedbackDevice.Analog)
        encoderContinuous(false)
        rawOffset(-224)
        sensorPhase(true)
        inverted(true)
        // coastMode()
        feedbackCoefficient = 0.3502
        /*(ctreController as TalonSRX)
            .sensorCollection
            .setAnalogPosition((ARM_OFFSET / feedbackCoefficient).toInt(), 20)
        */
        pid {
            p(0.002)
            //d(1.2)
            //f(3.3)
//            motionMagic(180.0, 80.0)
        }

        //   currentLimit(0, 0, 0) // TODO: determine limits
    }

    val ballMotor = MotorController(SparkMaxID(BALL_INTAKE)).config {
        feedbackCoefficient = 360.0 / 823.2
        setRawOffsetBasedOnAnalogConfig()
        //encoderType(FeedbackDevice.Analog)
        encoderContinuous(true) // todo
        //rawOffset(-224)  // todo
        sensorPhase(false)  // todo
        inverted(true) // todo
        pid {
            p(0.000075)
            d(0.00025)
            //f(3.3)
        }
    }

    private val extensionSolenoid = Solenoid(HATCH_EXTENSION)

    private val table = NetworkTableInstance.getDefault().getTable(name)

    /*var isClimbing = false
        set(value) {
            if (value != isClimbing) elevatorMotors.config(0) {
                pid {
                    if (value) {
                    }
                }
                        motionMagic(ELEVATOR_CLIMB_ACCELERATION, ELEVATOR_CLIMB_VELOCITY)
                    } else {
                        motionMagic(ELEVATOR_ACCELERATION, ELEVATOR_VELOCITY)
            }
            gearShifter.set(value)
            field = value
        }
    */
    var isLifting = false

    private val armRange: DoubleRange = 0.0..90.0 // degrees // TODO: measure arm range

    val angle: Angle
        get() = armMotor.position.degrees

    val intakeCurrent: Double
        get() = PDP.getCurrent(BALL_INTAKE)

    val isCarryingHatch: Boolean
        get() = hatchMotor.current > HATCH_CURRENT_THRESHOLD

    var isSetpointLow: Boolean = false


    var extending: Boolean
        get() = extensionSolenoid.get()
        set(value) {
            // if value=false, it's okay to set to false; if value=true, has to check if barm is in the way
            //if ((angle > Angle(85.0) && angleSetpoint > Angle(85.0)) || !value) {
            extensionSolenoid.set(value)
//            println("extending = $value. Hi")
        }

    var hatchCurrent: Double = -0.0
        get() = hatchMotor.current

    var ballCurrent: Double = 0.0
        get() = ballMotor.current

    var hasCargo = false

    var hasHatch = false

    // TODO: create safety feature to not let carm go when harm extended
    var angleSetpoint: Angle = angle
        set(value) {
            table.getEntry("Arm Error").setDouble(armMotor.closedLoopError)
            table.getEntry("Arm Output").setDouble(armMotor.output)

            field = value.asDegrees.coerceIn(armRange).degrees
            armMotor.setPositionSetpoint(field.asDegrees)
        }

    init {
        GlobalScope.launch(MeanlibDispatcher) {
            val angleEntry = table.getEntry("Angle")
            val angleSetpointEntry = table.getEntry("Angle Setpoint")
            ballMotor.setPositionSetpoint(ballMotor.position)
            periodic {
                angleEntry.setDouble(angle.asDegrees)
                angleSetpointEntry.setDouble(angleSetpoint.asDegrees)
                //if (/*DriverStation.getInstance().isEnabled && */!extending) {
//                    armMotor.setMotionMagicSetpoint(angleSetpoint.asDegrees)
                //println(angleSetpoint.asDegrees)
                // if(isEnabled) {
                // }
                //} else {
                //  armMotor.stop()
                // }
            }
        }
    }


    override suspend fun default() {
        var hasSpit = false
        var timer = Timer()
        periodic {
            intake(CARGO_HOLDING_INTAKE_POWER)
            grabber(HATCH_HOLDING_INTAKE_POWER)
            if (hasCargo) {
                if (isSetpointLow) {
                    angleSetpoint = LOW_ROCKET_ANGLE.degrees
                } else {
                    angleSetpoint = CARGO_SHIP_ANGLE.degrees
                }
            } else {
                angleSetpoint = HOME_ANGLE.degrees
            }

            if (driveRightTrigger > 0.15) {
                if (hasSpit == false) {
                    timer.start()
                }
                intake(OI.driveRightTrigger * -0.6 - 0.4)
                grabber(OI.driveRightTrigger * -0.7 - 0.4)
                hasSpit = true
                if (timer.get() > 0.3) {
                    extending = false
                }
            }
            if (driveRightTrigger < 0.10 && hasSpit) {
                hasCargo = false
                hasHatch = false
                hasSpit = false
                angleSetpoint = HOME_ANGLE.degrees
                grabber(0.0)
                intake(0.0)
                extending = false
            }
//            if (driveRightTrigger > 0.1) {
//                while ((hasSpit && OI.driveRightTrigger < 0.1) || OI.operatorController.leftBumper) {
//                    if (hasCargo) {
//                        hasCargo = false
//                    } else {
//                        hasHatch = false
//                    }
//
//                    intake(OI.driveRightTrigger * -0.6 - 0.4)
//                    grabber(OI.driveRightTrigger * -0.7 - 0.4)
//                    hasSpit = true
//
//                }
//                hasSpit = false
//                extending = false
//                grabber(0.0)
//                intake(0.0)
//            }
        }

    }


    fun intake(power: Double) {
        ballMotor.setPercentOutput(power)
    }

    fun grabber(power: Double) {
        //positive is in; negative is out
        hatchMotor.setPercentOutput(power)
    }

    override fun reset() {
        //intake(0.0)
        grabber(0.0)
    }

    suspend fun pullInCargoOrHatch() {
        periodic {
            if (OI.operatorController.rightTrigger > 0.1) {
                val gamePiecePower = OI.operatorController.rightTrigger * -0.6 - 0.4
                intake(gamePiecePower)
                grabber(gamePiecePower)
            } else {
                intake(CARGO_HOLDING_INTAKE_POWER)
                grabber(HATCH_HOLDING_INTAKE_POWER)
                this.stop()
            }
        }
    }

    suspend fun pushOutCargoOrHatch() {
        periodic {
            if (OI.operatorController.leftTrigger > 0.1) {
                val gamePiecePower = OI.operatorController.leftTrigger * 0.6 + 0.4
                intake(gamePiecePower)
                grabber(gamePiecePower)
            } else {
                intake(CARGO_HOLDING_INTAKE_POWER)
                grabber(HATCH_HOLDING_INTAKE_POWER)
                this.stop()
            }

        }
    }


}

/**
 * goes to the specified position w/o consideration of the ob1
 */
//suspend fun Barm.animate(angle: Angle, time: Time = 1.5.seconds) = use(this) {
//    println("Animating Barm to $angle")
//    val timer = Timer()
//    timer.start()
//    val startingAngle = Barm.angle
//    periodic {
//        val t = timer.get()
//        angleSetpoint = cubicMap(0.0, time.asSeconds, startingAngle.asDegrees, angle.asDegrees, t).degrees
//
//        if (t.seconds >= time) {
//            stop()
//        }
//    }
