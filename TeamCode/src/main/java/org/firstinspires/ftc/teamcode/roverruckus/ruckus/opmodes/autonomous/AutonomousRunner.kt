package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous

import android.content.Context
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new.TimeStampedData
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.subsystems.MecanumDriveTrain
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter

@Autonomous(name = "Ruckus Runner")
class AutonomousRunner : AutonomousBase(true) {

    val FILE_LEFT_SAMPLE = arrayOf("${TimeStampedData.FILE_PREFIX}sample_LEFT_CRATER", "${TimeStampedData.FILE_PREFIX}sample_LEFT_TOTEM", "${TimeStampedData.FILE_PREFIX}sample_LEFT_BACKUP")
    val FILE_MIDDLE_SAMPLE = arrayOf("${TimeStampedData.FILE_PREFIX}sample_MIDDLE_CRATER", "${TimeStampedData.FILE_PREFIX}sample_MIDDLE_TOTEM", "${TimeStampedData.FILE_PREFIX}sample_MIDDLE_BACKUP")
    val FILE_RIGHT_SAMPLE = arrayOf("${TimeStampedData.FILE_PREFIX}sample_RIGHT_CRATER", "${TimeStampedData.FILE_PREFIX}sample_RIGHT_TOTEM", "${TimeStampedData.FILE_PREFIX}sample_RIGHT_BACKUP")

    var toggleMode = 0

    override fun runOpMode() {

        val start_time = time

        while(opModeIsActive()) {
            val elapsed = time - start_time
            val data = USE_RECORD.pointsUntil(elapsed)
            var targetRotation = 0.0
            data.first.forEach {iv ->
                iv.bytes.forEach {
                    val device = hardwareMap.get(it.name)

                    when(device) {
                        is DcMotor -> {
                            device.mode = DcMotor.RunMode.RUN_USING_ENCODER
                            device.power = it.data[0]
                            device.targetPosition = it.data[1].toInt()
                        }
                        is Servo -> device.position = it.data[0]

                        is BNO055IMU -> targetRotation = it.data[0]
                    }
                }
            }
            if(data.second)break
        }
        DRIVETRAIN.stop()
    }

    private fun makeFile(file : String) {
        val f = File("${opMode().hardwareMap.appContext.filesDir}/$file")
        f.createNewFile()
    }

    private fun areFilesCreated() : Boolean {
        val filterParts = hardwareMap.appContext.fileList().filter { it.contains(TimeStampedData.FILE_PREFIX) }
        return filterParts.containsAll(listOf(FILE_LEFT_SAMPLE[0], FILE_LEFT_SAMPLE[1], FILE_LEFT_SAMPLE[2], FILE_MIDDLE_SAMPLE[0], FILE_MIDDLE_SAMPLE[1], FILE_MIDDLE_SAMPLE[2], FILE_RIGHT_SAMPLE[0], FILE_RIGHT_SAMPLE[1], FILE_RIGHT_SAMPLE[2]))
    }
}