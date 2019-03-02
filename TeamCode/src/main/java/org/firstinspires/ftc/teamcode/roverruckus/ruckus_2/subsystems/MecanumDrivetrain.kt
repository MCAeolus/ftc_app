package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.common.util.TelemetryUtil
import org.firstinspires.ftc.teamcode.common.util.math.Pose2d
import org.firstinspires.ftc.teamcode.common.util.math.Vector2d
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.Subsystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.MecanumWheelData
import java.util.*

class MecanumDrivetrain(hardware : HardwareMap) : Subsystem() {

    //
    //based off of paper on inverse kinematics for mecanum drivetrains.
    //https://www.chiefdelphi.com/t/paper-mecanum-and-omni-kinematic-and-force-analysis/106153
    //

    private val telemetryData = TelemetryData()
    private val motorPowers = Array(4) { _ -> 0.0 } //default of 0.0, V = [FL, BL, BR, FR]

    private var wheelRadius = 2 //in inches

    private val mecanumWheels = arrayOf(
            MecanumWheelData(Vector2d(1, -1), Vector2d(7, 7), HNAMES_RUCKUS.DRIVE_MOTORFL),
            MecanumWheelData(Vector2d(1, 1), Vector2d(-7, 7), HNAMES_RUCKUS.DRIVE_MOTORBL),
            MecanumWheelData(Vector2d(1, -1), Vector2d(-7, -7), HNAMES_RUCKUS.DRIVE_MOTORBR),
            MecanumWheelData(Vector2d(1, 1), Vector2d(7, -7), HNAMES_RUCKUS.DRIVE_MOTORFR)
    )

    var targetVelocity = Pose2d(0, 0, 0)
        private set

    init {
        for(i in 0..3) {
            val wheel = mecanumWheels[i]
            wheel.setMotor(hardware.get(DcMotor::class.java, wheel.motorName))
            wheel.motor().mode = DcMotor.RunMode.RUN_USING_ENCODER
            wheel.motor().zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        }

        mecanumWheels[0].motor().direction = DcMotorSimple.Direction.FORWARD
        mecanumWheels[1].motor().direction = DcMotorSimple.Direction.FORWARD
        mecanumWheels[2].motor().direction = DcMotorSimple.Direction.REVERSE
        mecanumWheels[3].motor().direction = DcMotorSimple.Direction.REVERSE
    }

    class TelemetryData {
        var frontLeftMotorPower = 0.0
        var frontRightMotorPower = 0.0
        var backLeftMotorPower = 0.0
        var backRightMotorPower = 0.0
    }

    override fun update(): LinkedHashMap<String, Any> {

        updateMotorPowers()

        for(i in 0..3)
            mecanumWheels[i].motor().power = motorPowers[i]

        telemetryData.frontLeftMotorPower = motorPowers[0]
        telemetryData.backLeftMotorPower = motorPowers[1]
        telemetryData.backRightMotorPower = motorPowers[2]
        telemetryData.frontRightMotorPower = motorPowers[3]

        return TelemetryUtil.convertToMap(telemetryData)
    }

    fun setVelocity(vec : Pose2d) {
        targetVelocity = vec
    }

    fun wheels() = mecanumWheels

    private fun updateMotorPowers() {
        for(i in 0..3) {
            val wheel = mecanumWheels[i]
            val rotationalVelocity = Vector2d(
             targetVelocity.x() - targetVelocity.heading*wheel.wheelPosition.y,
             targetVelocity.y() + targetVelocity.heading*wheel.wheelPosition.x
            )
            val wheelVelocity = rotationalVelocity.dot(wheel.rollerDirection) / wheelRadius
            motorPowers[i] = wheelVelocity
        }
    }

    fun stop() {
        setVelocity(Pose2d(0, 0, 0))
    }
}