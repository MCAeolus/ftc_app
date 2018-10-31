package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.common.util.SmidaTelemetry
import org.firstinspires.ftc.teamcode.common.util.TelemetryField
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem


@Autonomous(name="null class")
class NullRobotTest : LinearOpMode() {


    @TelemetryField lateinit var test : String
    @TelemetryField lateinit var lift : DcMotor


    override fun runOpMode() {
        lift = hardwareMap.get(DcMotor::class.java, HARDWARENAMES_MINIBOT.LIFT_MOTOR_RIGHT.v)
        test = "hi"
        val tele = SmidaTelemetry(true, true, this)
        waitForStart()

        tele.update()

        testhold(5000)

    }

    fun testhold(millis : Long) {
        val time = ElapsedTime()
        while(time.milliseconds() < millis && opModeIsActive()) { idle() }
    }
}