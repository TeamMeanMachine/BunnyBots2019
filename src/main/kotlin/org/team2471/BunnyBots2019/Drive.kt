package org.team2471.BunnyBots2019

import com.analog.adis16448.frc.ADIS16448_IMU
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.AnalogInput
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.team2471.frc.lib.actuators.MotorController
import org.team2471.frc.lib.actuators.SparkMaxID
import org.team2471.frc.lib.actuators.SparkMaxWrapper
import org.team2471.frc.lib.actuators.TalonID
import org.team2471.frc.lib.control.PDController
import org.team2471.frc.lib.coroutines.*
import org.team2471.frc.lib.framework.Subsystem
import org.team2471.frc.lib.framework.use
import org.team2471.frc.lib.math.Vector2
import org.team2471.frc.lib.math.round
import org.team2471.frc.lib.motion.following.SwerveDrive
import org.team2471.frc.lib.motion.following.drive
import org.team2471.frc.lib.motion_profiling.following.SwerveParameters
import org.team2471.frc.lib.units.*
import kotlin.math.absoluteValue
import kotlin.math.truncate
import kotlin.math.withSign

private var gyroOffset = 0.0.degrees

object Drive : Subsystem("Drive"), SwerveDrive {

    /**
     * Coordinates of modules
     * **/
    override val modules: Array<SwerveDrive.Module> = arrayOf(

        Module(
            MotorController(SparkMaxID(Sparks.DRIVE_FRONTLEFT)),
            MotorController(SparkMaxID(Sparks.STEER_FRONTLEFT)),
            Vector2(-7.0, 7.5),
            (-310.0).degrees,
            AnalogSensor.SWERVE_FRONT_LEFT
        ),


        Module(
            MotorController(SparkMaxID(Sparks.DRIVE_FRONTRIGHT)),
            MotorController(SparkMaxID(Sparks.STEER_FRONTRIGHT)),
            Vector2(7.0, 7.5),
            (-225.0).degrees,
            AnalogSensor.SWERVE_FRONT_RIGHT
        ),


        Module(
            MotorController(SparkMaxID(Sparks.DRIVE_BACKLEFT)),
            MotorController(SparkMaxID(Sparks.STEER_BACKLEFT)),
            Vector2(-7.0, -7.5),
            (-133.0).degrees,
            AnalogSensor.SWERVE_BACK_LEFT
        ),

        Module(
            MotorController(SparkMaxID(Sparks.DRIVE_BACKRIGHT)),
            MotorController(SparkMaxID(Sparks.STEER_BACKRIGHT)),
            Vector2(7.0, -7.5),
            (-47.0).degrees,
            AnalogSensor.SWERVE_BACK_RIGHT
        )
    )

    //val gyro: Gyro? = null
    val gyro: ADIS16448_IMU? = ADIS16448_IMU()

    override var heading: Angle
        get() = gyroOffset - ((gyro?.angle ?: 0.0).degrees.wrap())
        set(value) {
            gyroOffset = value
            gyro?.reset()
        }

    override val headingRate: AngularVelocity
        get() = -(gyro?.rate ?: 0.0).degrees.perSecond

    override var velocity = Vector2(0.0, 0.0)

    override var position = Vector2(0.0, 0.0)

    override var robotPivot = Vector2(0.0, 0.0)

    override val parameters: SwerveParameters = SwerveParameters(
        turningKP = 0.002,
        gyroRateCorrection = 0.002,
        kpPosition = 0.3,
        kdPosition = 0.15,
        kPositionFeedForward = 0.05,
        kpHeading = 0.0,//0.004,
        kdHeading = 0.0,//0.005,
        kHeadingFeedForward = 0.00125
    )

    init {
        SmartDashboard.setPersistent("Use Gyro")

        //SmartDashboard.putData("Gyro", gyro!!.getNavX())

        GlobalScope.launch(MeanlibDispatcher) {
            val table = NetworkTableInstance.getDefault().getTable(name)

            val headingEntry = table.getEntry("Heading")

            val xEntry = table.getEntry("X")
            val yEntry = table.getEntry("Y")

            val flAngleEntry = table.getEntry("Front Left Angle")
            val frAngleEntry = table.getEntry("Front Right Angle")
            val blAngleEntry = table.getEntry("Back Left Angle")
            val brAngleEntry = table.getEntry("Back Right Angle")
            val flSPEntry = table.getEntry("Front Left SP")
            val frSPEntry = table.getEntry("Front Right SP")
            val blSPEntry = table.getEntry("Back Left SP")
            val brSPEntry = table.getEntry("Back Right SP")

            periodic {
                flAngleEntry.setDouble(modules[0].angle.asDegrees)
                   frAngleEntry.setDouble(modules[1].angle.asDegrees)
                   blAngleEntry.setDouble(modules[2].angle.asDegrees)
                   brAngleEntry.setDouble(modules[3].angle.asDegrees)
                   flSPEntry.setDouble(modules[0].speed)
                   frSPEntry.setDouble(modules[1].speed)
                   blSPEntry.setDouble(modules[2].speed)
                   brSPEntry.setDouble(modules[3].speed)

                val (x, y) = position

                xEntry.setDouble(x)
                yEntry.setDouble(y)
                headingEntry.setDouble(heading.asDegrees)

//                println("Angle=${modules[0].angle}, Analog=${(modules[0] as Module).analogAngle.asDegrees}" )
            }
        }
    }

    fun zeroGyro() = gyro?.reset()

    override suspend fun default() {

        val limelightTable = NetworkTableInstance.getDefault().getTable("limelight")
        val xEntry = limelightTable.getEntry("tx")
        val angleEntry = limelightTable.getEntry("ts")
        val table = NetworkTableInstance.getDefault().getTable(name)

        periodic {
            drive(
                OI.driveTranslation,
                OI.driveRotation,
                if (Drive.gyro!=null) SmartDashboard.getBoolean("Use Gyro", true) && !DriverStation.getInstance().isAutonomous else false,
                Vector2(0.0,0.0),
                0.0
                // 0.3 // inputDamping
            )
        }
    }

    fun initializeSteeringMotors() {
        for (module in 0..3) {
            val module = (Drive.modules[module] as Module)
            module.turnMotor.setRawOffset(module.analogAngle)
        }
    }

    class Module(
        val driveMotor: MotorController,
        val turnMotor: MotorController,
        override val modulePosition: Vector2,
        override val angleOffset: Angle,
        private val analogAnglePort: Int
    ) : SwerveDrive.Module {
        companion object {
            private const val ANGLE_MAX = 983
            private const val ANGLE_MIN = 47

            private val P = 0.0 //0.0075 //0.010
            private val D = 0.0 //0.00075
        }

        override val angle: Angle
            get() = turnMotor.position.degrees + angleOffset

        private val analogAngleInput = AnalogInput(analogAnglePort)

        val analogAngle: Angle
            get() = ((analogAngleInput.value - 170.0) / (3888.0-170.0) * 360.0).degrees

        val driveCurrent: Double
            get() = driveMotor.current

        private val pdController = PDController(P, D)

        override val speed: Double
            get() = driveMotor.velocity

        override val currDistance: Double
            get() = driveMotor.position

        override var prevDistance: Double = 0.0

        override fun zeroEncoder() {
            driveMotor.position = 0.0
        }

        override var angleSetpoint: Angle = 0.0.degrees
            set(value) = turnMotor.setPositionSetpoint((angle + (value-angle).wrap() - angleOffset).asDegrees)

        override fun setDrivePower(power: Double) {
            driveMotor.setPercentOutput(power)
        }


        val error: Angle
            get() = turnMotor.closedLoopError.degrees

        init {
            turnMotor.config(20) {
                // this was from lil bois bench test of swerve
                feedbackCoefficient = 360.0 / 823.2
                setRawOffsetConfig(analogAngle)
                inverted(true)
                setSensorPhase(false)
                pid {
                    p(0.000075)
                    d(0.00025)
                }
            }
            driveMotor.config {
                coastMode() //brakeMode()
                feedbackCoefficient = 1.0 / 6.125
                currentLimit(30, 0, 0)
                openLoopRamp(0.15)
            }

            GlobalScope.launch {
                val table = NetworkTableInstance.getDefault().getTable(name)
                val pSwerveEntry = table.getEntry("Swerve P").apply {
                    setPersistent()
                    setDefaultDouble(0.0075)
                }
                val dSwerveEntry = table.getEntry("Swerve D").apply {
                    setPersistent()
                    setDefaultDouble(0.00075)
                }
//                val pdController2 = PDController(pSwerveEntry.getDouble(0.0075),
//                    dSwerveEntry.getDouble(0.00075))

            }
        }

        override fun driveWithDistance(angle: Angle, distance: Length) {
            driveMotor.setPositionSetpoint(distance.asFeet)
            val error = (angle - this.angle).wrap()
            pdController.update(error.asDegrees)
        }

        override fun stop() {
            driveMotor.stop()
            //turnMotor.stop()
        }
    }
}

suspend fun Drive.driveDistance(distance: Length, speed: Double) = use(Drive) {
    // TODO: fix this
    val initialPosition = position
    periodic {
        drive(Vector2(0.0, speed.withSign(distance.asInches)), 0.0, false)

        val traveled = initialPosition.distance(position)
        println("Position: $position, initial: $initialPosition, distance: $traveled")
        if (traveled > distance.asFeet.absoluteValue) stop()
    }
}

suspend fun Drive.driveTime(translation: Vector2, time: Time) = use(Drive) {
    val timer = Timer().apply { start() }
    periodic {
        drive(translation, 0.0, false)
        if (timer.get() > time.asSeconds) stop()
    }
}

suspend fun Drive.turnToAngle(angle: Angle) = use(this){
    val kTurn = 0.007
    periodic {
        val turnError = (angle - heading).wrap()
        Drive.drive(Vector2(0.0,0.0), turnError.asDegrees * kTurn)
    }
}