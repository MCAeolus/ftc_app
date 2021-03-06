package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.drivetrain.DummyDriveTrain
import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.common.util.SmidaTelemetry
import org.firstinspires.ftc.teamcode.common.util.TelemetryField
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.IntakeMachine
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.LiftMachine
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain

//@TeleOp(name = "Telemetry and Debugging")
class TelemetryOpMode : Robot(DummyDriveTrain(), mapOf("IMU" to IMU(), "INTAKE" to IntakeMachine(), "LIFT" to LiftMachine())) {

    @TelemetryField
    lateinit var IMU : IMU
    @TelemetryField
    lateinit var INTAKE : IntakeMachine
    @TelemetryField
    lateinit var LIFT : LiftMachine

    lateinit var TELEM : SmidaTelemetry

    override fun start() {
        IMU = COMPONENTS["IMU"] as IMU
        INTAKE = COMPONENTS["INTAKE"] as IntakeMachine
        LIFT = COMPONENTS["LIFT"] as LiftMachine
        //INTAKE.armMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        TELEM = SmidaTelemetry(true, true, this)
    }

    override fun loop() {
        TELEM.update()


        /**if(gamepad1.x) {
            INTAKE.armMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            INTAKE.armMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }**/
    }

}