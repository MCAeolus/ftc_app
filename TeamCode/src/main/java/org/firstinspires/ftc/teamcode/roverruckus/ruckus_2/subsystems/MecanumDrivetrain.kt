package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.subsystems

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.util.TelemetryUtil
import org.firstinspires.ftc.teamcode.common.util.math.Pose2d
import org.firstinspires.ftc.teamcode.common.util.math.Vector2d
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.Subsystem
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.util.MecanumWheelData
import java.util.*
import kotlin.math.absoluteValue

class MecanumDrivetrain(hardware : HardwareMap, private val robot : RobotInstance) : Subsystem() {

    //
    //based off of paper on inverse kinematics for mecanum drivetrains.
    //https://www.chiefdelphi.com/t/paper-mecanum-and-omni-kinematic-and-force-analysis/106153
    //

    private val telemetryData = TelemetryData()

    private var wheelRadius = 2 //in inches

    private val mecanumWheels = arrayOf( //V = [FL, BL, BR, FR]
            MecanumWheelData(Vector2d(1, -1), Vector2d(7, 7), HNAMES_RUCKUS.DRIVE_MOTORFL),
            MecanumWheelData(Vector2d(1, 1), Vector2d(-7, 7), HNAMES_RUCKUS.DRIVE_MOTORBL),
            MecanumWheelData(Vector2d(1, -1), Vector2d(-7, -7), HNAMES_RUCKUS.DRIVE_MOTORBR),
            MecanumWheelData(Vector2d(1, 1), Vector2d(7, -7), HNAMES_RUCKUS.DRIVE_MOTORFR)
    )

    private val imu : BNO055IMU

    private val mecanumVelocityFields = arrayOf(
            TelemetryData::frontLeftMotorPower,
            TelemetryData::backLeftMotorPower,
            TelemetryData::backRightMotorPower,
            TelemetryData::frontRightMotorPower
    )

    var targetVelocity = Pose2d(0, 0, 0) //as received... may not be physically attainable
        private set

    var currentPosition = Pose2d(0, 0, 0) //this is an estimate
        private set

    //val K = mecanumWheels[0].wheelPosition.x.absoluteValue + mecanumWheels[0].wheelPosition.y.absoluteValue //wheel 0 will be our reference wheel (since all have the same distance magnitude)
    //referenced out because I don't need to estimate the position heading

    private var lastWheelRotations : Array<Double> //to find current pose

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

        val imuParameters = BNO055IMU.Parameters()

        imuParameters.angleUnit = BNO055IMU.AngleUnit.RADIANS //we're using this for vector math... so should keep as radians.
        imuParameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        imuParameters.loggingEnabled = true
        imuParameters.loggingTag = "IMU"
        imuParameters.accelerationIntegrationAlgorithm = JustLoggingAccelerationIntegrator()

        imu = hardware.get(com.qualcomm.hardware.bosch.BNO055IMU::class.java, HNAMES_RUCKUS.IMU_NAME)
        imu.initialize(imuParameters)


        lastWheelRotations = getWheelRotations()
    }

    class TelemetryData {
        var frontLeftMotorPower = 0.0
        var frontRightMotorPower = 0.0
        var backLeftMotorPower = 0.0
        var backRightMotorPower = 0.0
    }

    override fun update(): LinkedHashMap<String, Any> {

        updateMotorPowers()

        mecanumWheels.forEach { it.applyPower() }

        //update telemetry data
        for(i in 0..3) mecanumVelocityFields[i].set(telemetryData, mecanumWheels[i].motorPower)

        updatePosition()

        return TelemetryUtil.convertToMap(telemetryData)
    }

    fun setVelocity(vec : Pose2d) { //this is used as the setter in the case that modifications should occur to the velocity before updating it.
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
            wheel.motorPower = wheelVelocity
        }
    }

    private fun updatePosition() { //this is an ESTIMATE, using averages due to scrubbing error.
        val wheelRotations = getWheelRotations()

        val rotationDeltas = Array(4){_ -> 0.0}
        for(i in 0..3) rotationDeltas[i] = wheelRotations[i] - lastWheelRotations[i]

        lastWheelRotations = wheelRotations

        val positionalDelta = getPoseDelta(rotationDeltas)
        val fieldDelta = positionalDelta.rotate(getRobotHeading().toDouble(), Vector2d.AngleUnit.RADIANS)

        currentPosition = currentPosition.add(Pose2d(fieldDelta, getRobotHeading().toDouble()))
    }

    //forward kinematics problem from paper.
    fun getPoseDelta(rotationDeltas : Array<Double>) : Vector2d {

        val x = wheelRadius * (rotationDeltas[0] + rotationDeltas[1] + rotationDeltas[2] + rotationDeltas[3])
        val y = wheelRadius * (-rotationDeltas[0] + rotationDeltas[1] - rotationDeltas[2] + rotationDeltas[3])

        return Vector2d(x, y)
    }

    fun getRobotHeading() : Float = imu.angularOrientation.firstAngle

    private fun getWheelRotations() : Array<Double> {
        val wheelRotations = Array(4){_ -> 0.0} //initialize array

        for(i in 0..3) {
            val wheel = mecanumWheels[i]
            wheelRotations[i] = (2 * Math.PI * wheel.motor().currentPosition) / wheel.motor().motorType.ticksPerRev //now in radians
        }

        return wheelRotations
    }

    fun resetEncoders() {
        mecanumWheels.forEach {
            it.motor().mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            it.motor().mode = DcMotor.RunMode.RUN_USING_ENCODER
        }
    }

    fun stop() {
        setVelocity(Pose2d(0, 0, 0))
    }
}