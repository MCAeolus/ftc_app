package org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import org.firstinspires.ftc.teamcode.common.common_machines.IMachine
import org.firstinspires.ftc.teamcode.common.drivetrain.IDriveTrain
import org.firstinspires.ftc.teamcode.common.robot.IRobot
import org.firstinspires.ftc.teamcode.common.util.Trackable
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.HNAMES_RUCKUS
import kotlin.reflect.KClass

class MecanumDriveTrain : IDriveTrain, Trackable {

    lateinit var motorFL : DcMotor
    lateinit var motorFR : DcMotor
    lateinit var motorBL : DcMotor
    lateinit var motorBR : DcMotor

    override fun init(robot: IRobot) {
        motorFL = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.DRIVE_MOTORFL)
        motorFR = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.DRIVE_MOTORFR)
        motorBL = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.DRIVE_MOTORBL)
        motorBR = robot.opMode().hardwareMap.get(DcMotor::class.java, HNAMES_RUCKUS.DRIVE_MOTORBR)

        motorFL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorFR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorBL.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        motorBR.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE

        motorFL.direction = DcMotorSimple.Direction.REVERSE
        motorBL.direction = DcMotorSimple.Direction.REVERSE

        motorFL.mode = DcMotor.RunMode.RUN_USING_ENCODER
        motorFR.mode = DcMotor.RunMode.RUN_USING_ENCODER
        motorBL.mode = DcMotor.RunMode.RUN_USING_ENCODER
        motorBR.mode = DcMotor.RunMode.RUN_USING_ENCODER
    }

    override fun move(x: Double, y: Double, r: Double, p: Double) {

        val pwr = Math.hypot(x, y)
        val angle = Math.atan2(y, x) - (Math.PI / 4)

        val FL = pwr * (Math.cos(angle)) + r
        val FR = pwr * (Math.sin(angle)) - r
        val BL = pwr * (Math.sin(angle)) + r
        val BR = pwr * (Math.cos(angle)) - r

        powerSet(FL * p, FR * p, BL * p, BR * p)

    }

    override fun motorList(): List<DcMotor> = listOf(motorFL, motorFR, motorBL, motorBR)

    override fun motorMap(): Map<String, DcMotor> = linkedMapOf(
                            HNAMES_RUCKUS.DRIVE_MOTORFL to motorFL,
                            HNAMES_RUCKUS.DRIVE_MOTORFR to motorFR,
                            HNAMES_RUCKUS.DRIVE_MOTORBL to motorBL,
                            HNAMES_RUCKUS.DRIVE_MOTORBR to motorBR)

    override fun driveClass(): KClass<out IDriveTrain> = this::class

    override fun stop() {
        powerSet(0.0, 0.0, 0.0, 0.0)
    }

    fun powerSet(fl: Double, fr: Double, bl: Double, br: Double) {
        motorFL.power = fl
        motorFR.power = fr
        motorBL.power = bl
        motorBR.power = br
    }

    fun resetEncoders() {
        for(m in motorList()) {
            m.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            m.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }
    }

    override fun data(): Map<String, Any> = linkedMapOf()

}