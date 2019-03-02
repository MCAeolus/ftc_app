package org.firstinspires.ftc.teamcode.common.util

import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType

class DummyDcMotor : DcMotor {
    override fun setMotorType(motorType: MotorConfigurationType?) {
    }

    override fun resetDeviceConfigurationForOpMode() {
    }

    override fun getController(): DcMotorController {
        return DummyDcMotorController()
    }

    override fun getDeviceName(): String {
        return "N/A"
    }

    override fun getCurrentPosition(): Int {
        return 0
    }

    override fun setTargetPosition(position: Int) {

    }

    override fun getPowerFloat(): Boolean {
        return true
    }

    override fun getConnectionInfo(): String {
        return "N/A"
    }

    override fun getVersion(): Int {
        return 0
    }

    override fun getMode(): DcMotor.RunMode {
        return DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    override fun getPower(): Double {
        return 0.0
    }

    override fun getPortNumber(): Int {
        return 0
    }

    override fun isBusy(): Boolean {
        return false
    }

    override fun close() {
    }

    override fun getManufacturer(): HardwareDevice.Manufacturer {
        return HardwareDevice.Manufacturer.Unknown
    }

    override fun setPowerFloat() {
    }

    override fun setPower(power: Double) {
    }

    override fun setMode(mode: DcMotor.RunMode?) {
    }

    override fun setDirection(direction: DcMotorSimple.Direction?) {
    }

    override fun getMotorType(): MotorConfigurationType {
        return MotorConfigurationType.getUnspecifiedMotorType()
    }

    override fun getTargetPosition() = 0

    override fun getDirection(): DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD

    override fun setZeroPowerBehavior(zeroPowerBehavior: DcMotor.ZeroPowerBehavior?) {
    }

    override fun getZeroPowerBehavior() = DcMotor.ZeroPowerBehavior.UNKNOWN

}