package org.firstinspires.ftc.teamcode.roverruckus.minibit.robotmodes

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.common.util.SmidaTelemetry
import org.firstinspires.ftc.teamcode.common.util.TelemetryField
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.MiniTankDrive

@TeleOp(name="Arcade Test")@Disabled
class ArcadeControlOP : Robot(MiniTankDrive(), mapOf("IMU" to IMU())) {

    @TelemetryField
    lateinit var IMU : IMU

    val tele = SmidaTelemetry(true, true, this)

    override fun start() {

        IMU = COMPONENTS["IMU"] as IMU
    }

    override fun loop() {
        tele.update()

        val z = IMU.XYZ().thirdAngle

        if(z >= -90 || z <= 90)
            DRIVETRAIN.move(0.0, -gamepad1.left_stick_y.toDouble(), -gamepad1.right_stick_x.toDouble())
        else
            DRIVETRAIN.move(0.0, gamepad1.left_stick_y.toDouble(), gamepad1.right_stick_x.toDouble())

    }
}