package org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.replay_v2

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.CRServo
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.teamcode.common.util.math.Pose2d
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain
import org.firstinspires.ftc.teamcode.roverruckus.ruckus_2.RobotInstance

@Autonomous(name="Editor Player")
class EditorPlayer : LinearOpMode() {

    lateinit var robot : RobotInstance

    override fun runOpMode() {
        robot = RobotInstance(this)
        robot.start()

        while (!isStarted) {
            telemetry.addData("File", if (RecordingPreferences.filePath == "") "not selected." else RecordingPreferences.filePath)
            telemetry.update()
        }

        if (RecordingPreferences.filePath == "") requestOpModeStop()
        val RECORD = ReplayFile.DataStream(RecordingPreferences.filePath, hardwareMap)

        telemetry.addData("File status", "loading data.")
        telemetry.update()
        RECORD.load()
        telemetry.addData("File status", "preparing data.")
        telemetry.update()
        RECORD.prepare()

        telemetry.addData("File status", "finished loading data.")
        telemetry.update()

        robot.mecanumDrive.resetEncoders()

        val startTime = time
        while (opModeIsActive()) {
            val elapsed = time - startTime

            telemetry.addData("elapsed time", elapsed)
            telemetry.update()

            val data = RECORD.pointsUntil(elapsed)

            if(gamepad1.x) {
                RECORD.trim(0.0, elapsed)
                telemetry.log().add("Recording trimmed at $elapsed seconds.")
                robot.stop()
                RECORD.write()
                break
            }

            data.first.forEach { iv ->
                iv.bytes.forEach {
                    robot.subsystems[it.name]!!.updateFromReplay(it.data)
                }
            }

            robot.update()
            if (data.second) break
        }
        robot.stop()
    }

}