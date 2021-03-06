package org.firstinspires.ftc.teamcode.roverruckus.minibit.robotmodes

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.common_machines.IMU
import org.firstinspires.ftc.teamcode.common.robot.Robot
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.IntakeSystem
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.LiftSystem
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.MiniTankDrive
import org.firstinspires.ftc.teamcode.roverruckus.minibit.machine.TotemDropper

@TeleOp(name = "Minibot-Teleop")@Disabled
class TeleopMinibot : Robot(MiniTankDrive(), mapOf("Lift" to LiftSystem(), "IMU" to IMU(), "Intake" to IntakeSystem(), "Drop" to TotemDropper())) {

    lateinit var LIFT_SYSTEM : LiftSystem
    lateinit var IMU : IMU
    lateinit var INTAKE : IntakeSystem

    lateinit var TOTEM_DROPPER : TotemDropper

    var a1_pressed = false
    var x1_pressed = false

    var hook_pos = LiftSystem.HookPosition.HOOKED
    var b1_pressed = false

    var slow_drivetrain = false
    var slow_arm = false

    var lstick1_pressed = false
    var rstick1_pressed = false

    override fun start() {
        LIFT_SYSTEM = COMPONENTS["Lift"] as LiftSystem
        LIFT_SYSTEM.lift_motorR.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER

        LIFT_SYSTEM.lift_motorL.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        IMU = COMPONENTS["IMU"] as IMU
        INTAKE = COMPONENTS["Intake"] as IntakeSystem

        TOTEM_DROPPER = COMPONENTS["Drop"] as TotemDropper
    }

    var dashboard = FtcDashboard.getInstance()

    override fun loop() {

        telemetry.addData("gamepad l y", gamepad1.left_stick_y)
        telemetry.addData("gamepad 1 r", gamepad1.right_stick_x)

        DRIVETRAIN.move(0.0, gamepad1.left_stick_y.toDouble(), -gamepad1.right_stick_x.toDouble(), if(slow_drivetrain)0.5 else 1.0)

        /**
        if(gamepad1.left_trigger > 0 && !(gamepad1.right_trigger > 0)){
            LIFT_SYSTEM.manual_run(-gamepad1.left_trigger.toDouble() * if(slow_arm)0.5 else 1.0)
        }else if(!(gamepad1.left_trigger > 0) && gamepad1.right_trigger > 0){
            LIFT_SYSTEM.manual_run(gamepad1.right_trigger.toDouble() * if(slow_arm) 0.5 else 1.0)
        }else {
            LIFT_SYSTEM.manual_run(0.0)
        }
        **/

        if(gamepad1.left_trigger > 0 && !(gamepad1.left_bumper)) {
            LIFT_SYSTEM.manual_run(-gamepad1.left_trigger.toDouble() * if(slow_arm)0.5 else 1.0)
        }else if(gamepad1.left_bumper && !(gamepad1.left_trigger > 0)) {
            LIFT_SYSTEM.manual_run(1.0 * if(slow_arm)0.5 else 1.0)
        } else
            LIFT_SYSTEM.manual_run(0.0)

        if(gamepad1.right_bumper && !(gamepad1.right_trigger > 0)) {
            INTAKE.runMotors(true)
        }else if(gamepad1.right_trigger > 0 && !gamepad1.right_bumper)
            INTAKE.runMotors(false)
        else
            INTAKE.stop()


        /**
        if(gamepad1.left_bumper && !gamepad1.right_bumper)
            INTAKE.runMotors(true)
        else if(gamepad1.right_bumper && !gamepad1.left_bumper)
            INTAKE.runMotors(false)
        else
            INTAKE.stop()
        **/

        if(gamepad1.b && !b1_pressed) {
            b1_pressed = true
            hook_pos = if(hook_pos == LiftSystem.HookPosition.HOOKED) LiftSystem.HookPosition.UNHOOKED else LiftSystem.HookPosition.HOOKED
            LIFT_SYSTEM.setHookPosition(hook_pos)
        }else if(!gamepad1.b && b1_pressed)
            b1_pressed = false

        if(gamepad1.left_stick_button && !lstick1_pressed) {
            lstick1_pressed = true
            slow_drivetrain = !slow_drivetrain
        }else if(!gamepad1.left_stick_button && lstick1_pressed)
            lstick1_pressed = false

        if(gamepad1.right_stick_button && !rstick1_pressed) {
            rstick1_pressed = true
            slow_arm = !slow_arm
        }else if(!gamepad1.right_stick_button && rstick1_pressed)
            rstick1_pressed = false


        telemetry()
    }

    fun telemetry() {
        val packet = TelemetryPacket()
        for(m in DRIVETRAIN.motorMap())
            packet.put(m.key, m.value.currentPosition)
        packet.putAll(
                mapOf<String, Any>(
                        "Left lift motor" to LIFT_SYSTEM.lift_motorL.currentPosition,
                        "Right lift motor" to LIFT_SYSTEM.lift_motorR.currentPosition,

                        "Left hook servo" to LIFT_SYSTEM.hook_left.position,
                        "Right hook servo" to LIFT_SYSTEM.hook_right.position,

                        "IMU z 180" to IMU.XYZ().thirdAngle,
                        "IMU z 360" to IMU.getZ360()
                )
        )

        dashboard.sendTelemetryPacket(packet)
    }
}