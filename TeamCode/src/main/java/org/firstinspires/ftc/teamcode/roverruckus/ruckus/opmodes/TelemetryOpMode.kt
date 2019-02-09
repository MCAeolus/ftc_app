package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.drivetrain.DummyDriveTrain
import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.common.util.SmidaTelemetry
import org.firstinspires.ftc.teamcode.common.util.TelemetryField
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain

@TeleOp(name = "Telemetry and Debugging")
class TelemetryOpMode : Robot(DummyDriveTrain(), mapOf("IMU" to IMU())) {

    @TelemetryField
    lateinit var IMU : IMU

    lateinit var TELEM : SmidaTelemetry

    override fun start() {
        IMU = COMPONENTS["IMU"] as IMU
        TELEM = SmidaTelemetry(true, true, this)
    }

    override fun loop() = TELEM.update()

}