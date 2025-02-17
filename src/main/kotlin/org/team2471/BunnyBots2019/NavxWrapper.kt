package org.team2471.BunnyBots2019

import com.kauailabs.navx.frc.AHRS
import edu.wpi.first.wpilibj.SPI
import edu.wpi.first.wpilibj.interfaces.Gyro

class NavxWrapper : Gyro {
    private val navx = AHRS(SPI.Port.kMXP)

    override fun getAngle(): Double = -navx.yaw.toDouble()

    override fun getRate(): Double = navx.rawGyroZ.toDouble()

    override fun close() {
        navx.close()
    }

    override fun calibrate() = Unit

    override fun reset() = navx.reset()

    override fun free() = navx.free()

    fun getNavX() = navx
}