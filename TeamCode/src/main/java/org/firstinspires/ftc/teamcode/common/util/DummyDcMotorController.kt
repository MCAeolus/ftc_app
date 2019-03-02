package org.firstinspires.ftc.teamcode.common.util

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorController
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType

class DummyDcMotorController : DcMotorController {

    override fun setMotorType(motor: Int, motorType: MotorConfigurationType?) {

    }

    override fun setMotorMode(motor: Int, mode: DcMotor.RunMode?) {
    }

    override fun resetDeviceConfigurationForOpMode(motor: Int) {
    }

    override fun resetDeviceConfigurationForOpMode() {
    }

    override fun setMotorZeroPowerBehavior(motor: Int, zeroPowerBehavior: DcMotor.ZeroPowerBehavior?) {
    }

    override fun getMotorCurrentPosition(motor: Int): Int {
        return 0
    }

    override fun getMotorZeroPowerBehavior(motor: Int): DcMotor.ZeroPowerBehavior {
        return DcMotor.ZeroPowerBehavior.UNKNOWN
    }

    override fun getMotorType(motor: Int): MotorConfigurationType {
        return MotorConfigurationType.getUnspecifiedMotorType()
    }

    override fun getMotorPower(motor: Int): Double {
        return 0.0
    }

    override fun getDeviceName(): String {
        return "N/A"
    }

    override fun getConnectionInfo(): String {
        return "N/A"
    }

    override fun getVersion(): Int {
        return 0
    }

    override fun setMotorPower(motor: Int, power: Double) {
    }

    override fun getMotorMode(motor: Int): DcMotor.RunMode {
        return DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    override fun isBusy(motor: Int): Boolean {
        return false
    }

    override fun close() {
    }

    override fun getMotorPowerFloat(motor: Int): Boolean {
        return true
    }

    override fun getMotorTargetPosition(motor: Int): Int {
        return 0
    }

    override fun setMotorTargetPosition(motor: Int, position: Int) {
    }

    override fun getManufacturer(): HardwareDevice.Manufacturer {
        return HardwareDevice.Manufacturer.Unknown
    }
}