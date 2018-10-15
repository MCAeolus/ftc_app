package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.drivetrain.util.PID
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT
import kotlin.math.PI
import kotlin.math.pow

@Autonomous(name="PID TEST")
class PIDTest : AutonomousBase() {

    val ENCODER_TICKS = 1120
    val WHEEL_IN_DIAM = 4
    var initial_angle = 0F

    val TICKS_IN = ENCODER_TICKS / (WHEEL_IN_DIAM * PI)

    val move_controllerPID = PID(1F, 0.5F, 1F)
    val rot_controllerPID = PID(1F, 0.5F, 1F)


    override fun runOpMode() {
        super.runOpMode()
        initial_angle = IMU.getZ360()
        waitForStart()

        PID_rot(90.0 + initial_angle)
    }

    fun PID_drive(x : Double, y : Double) {
        move_controllerPID.stop()
        val angle = Math.toDegrees(Math.atan2(y, x))
        val dist = Math.hypot(x, y)

        val dist_enc = TICKS_IN * dist + DRIVETRAIN.motorList()[0].currentPosition

        PID_rot(angle)

        while(Math.abs(dist_enc - DRIVETRAIN.motorList()[0].currentPosition) > 2){
            val pid_calc = move_controllerPID.calculate(dist_enc.toFloat(), DRIVETRAIN.motorList()[0].currentPosition.toFloat())
            DRIVETRAIN.move(0.0, pid_calc, 0.0)
        }
        DRIVETRAIN.stop()
    }

    fun PID_combined_drive(vx : Double, vy : Double){
        move_controllerPID.stop()
        rot_controllerPID.stop()

        resetDriveTrainEncoders(DcMotor.RunMode.RUN_USING_ENCODER)

        val angle = Math.toDegrees(Math.atan2(vy, vx))
        val dist = Math.hypot(vx, vy)

        val desired_x = vx * TICKS_IN
        val desired_y = vy * TICKS_IN

        val enc_dist = TICKS_IN * dist
        var enc_post = 0

        var actual_x = 0.0
        var actual_y = 0.0

        while((Math.abs(actual_x - desired_x) > 2 || Math.abs(actual_y - desired_y) > 2)
            && Math.abs(angle - IMU.getZ360()) > 2){
            val dEnc = DRIVETRAIN.motorList()[0].currentPosition - enc_post
            enc_post = DRIVETRAIN.motorList()[0].currentPosition
            val ang = IMU.getZ360()

            val x = dEnc * Math.cos(Math.toRadians(ang.toDouble()))
            val y = dEnc * Math.sin(Math.toRadians(ang.toDouble()))

            actual_x += x
            actual_y += y

            val move_pow = move_controllerPID.calculate(enc_dist.toFloat(), Math.sqrt(actual_x.pow(2) + actual_y.pow(2)).toFloat())
            val rot_pow = rot_controllerPID.calculate(angle.toFloat(), ang)

            DRIVETRAIN.move(0.0, move_pow, rot_pow)


        }
        DRIVETRAIN.stop()
    }

    fun PID_rot(angle : Double) {
        rot_controllerPID.stop()
        val const = 0.0

        while(Math.abs(angle - IMU.getZ360()) > 3){
            val pow = rot_controllerPID.calculate(angle.toFloat(), IMU.getZ360())
            telemetry.addData("pid", pow)
            telemetry.addData("pid err", rot_controllerPID.ERROR_POST)
            telemetry.update()
            DRIVETRAIN.move(0.0, const, pow)
        }
        DRIVETRAIN.stop()
    }

    fun resetDriveTrainEncoders(final_mode : DcMotor.RunMode){
        for(m in DRIVETRAIN.motorList()){
            m.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            m.mode = final_mode
        }
    }

}