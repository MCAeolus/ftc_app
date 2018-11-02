package org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.common.drivetrain.util.PID
import org.firstinspires.ftc.teamcode.roverruckus.minibit.HARDWARENAMES_MINIBOT
import org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous.PIDTest.PIDTesting.distance_forward
import org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous.PIDTest.PIDTesting.distance_rotation
import org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous.PIDTest.PIDTesting.moveD
import org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous.PIDTest.PIDTesting.moveI
import org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous.PIDTest.PIDTesting.moveK
import org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous.PIDTest.PIDTesting.rotD
import org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous.PIDTest.PIDTesting.rotI
import org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous.PIDTest.PIDTesting.rotK
import kotlin.math.PI
import kotlin.math.pow

@Autonomous(name="PID TEST")
class PIDTest : AutonomousBase(true, false) {


    val move_controllerPID = PID(1F, 0.5F, 1F)

    val ENCODER_TICKS = 1120
    val WHEEL_IN_DIAM = 4
    var initial_angle = 0F

    val TICKS_IN = ENCODER_TICKS / (WHEEL_IN_DIAM * PI)

    @Config
    object PIDTesting
    {
        @JvmField var distance_forward = 2.0
        @JvmField var distance_rotation = 90.0

        @JvmField var rotK = 0.7
        @JvmField var rotI = 0.5
        @JvmField var rotD = 0.2

        @JvmField var moveK = 1.0
        @JvmField var moveI = 0.5
        @JvmField var moveD = 1.0
    }


    override fun runOpMode() {
        super.runOpMode()
        initial_angle = IMU.getZ360()
        waitForStart()
}

    fun PID_drive(dist : Float) {
        move_controllerPID.stop()

        val dist_enc = TICKS_IN * dist + DRIVETRAIN.motorList()[0].currentPosition

        while(Math.abs(dist_enc - DRIVETRAIN.motorList()[0].currentPosition) > 2){
            val pid_calc = move_controllerPID.calculate(dist_enc.toFloat(), DRIVETRAIN.motorList()[0].currentPosition.toFloat())
            telemetryUpdate(mapOf(
                    "Calculated Error (val)" to move_controllerPID.ERROR_POST,
                    "Calculated Rotation PID (val)" to pid_calc,
                    "Desired Encoder Position (val)" to dist_enc,
                    "Current Encoder Position (val)" to DRIVETRAIN.motorList()[0].currentPosition,
                    "Current Time (seconds)" to move_controllerPID.TIME.seconds()
            ))
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

fun resetDriveTrainEncoders(final_mode : DcMotor.RunMode){
        for(m in DRIVETRAIN.motorList()){
            m.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            m.mode = final_mode
        }
    }
}