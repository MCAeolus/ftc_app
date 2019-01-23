package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous

import android.content.Context
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous.playback_new.TimeStampedData
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter

@Autonomous(name = "Ruckus Runner")
class AutonomousRunner : AutonomousBase() {

    val FILE_LEFT_SAMPLE = arrayOf("${TimeStampedData.FILE_PREFIX}sample_LEFT", "${TimeStampedData.FILE_PREFIX}sample_LEFT_BACKUP")
    val FILE_MIDDLE_SAMPLE = arrayOf("${TimeStampedData.FILE_PREFIX}sample_MIDDLE", "${TimeStampedData.FILE_PREFIX}sample_MIDDLE_BACKUP")
    val FILE_RIGHT_SAMPLE = arrayOf("${TimeStampedData.FILE_PREFIX}sample_RIGHT", "${TimeStampedData.FILE_PREFIX}sample_RIGHT_BACKUP")

    var isBACKUP = 0

    override fun runOpMode() {
        super.runOpMode()

        if(!areFilesCreated()) {
            makeFile(FILE_LEFT_SAMPLE[0])
            makeFile(FILE_LEFT_SAMPLE[1])
            makeFile(FILE_MIDDLE_SAMPLE[0])
            makeFile(FILE_MIDDLE_SAMPLE[1])
            makeFile(FILE_RIGHT_SAMPLE[0])
            makeFile(FILE_RIGHT_SAMPLE[1])
        }

        var lastPress = -1.0
        var doUpdate = false

        var RECORD_LEFT = TimeStampedData.DataStream(FILE_LEFT_SAMPLE[isBACKUP], hardwareMap)
        var RECORD_MIDDLE = TimeStampedData.DataStream(FILE_MIDDLE_SAMPLE[isBACKUP], hardwareMap)
        var RECORD_RIGHT = TimeStampedData.DataStream(FILE_RIGHT_SAMPLE[isBACKUP], hardwareMap)

        while(!isStarted) {
            if(doUpdate) {
                RECORD_LEFT = TimeStampedData.DataStream(FILE_LEFT_SAMPLE[isBACKUP], hardwareMap)
                RECORD_MIDDLE = TimeStampedData.DataStream(FILE_MIDDLE_SAMPLE[isBACKUP], hardwareMap)
                RECORD_RIGHT = TimeStampedData.DataStream(FILE_RIGHT_SAMPLE[isBACKUP], hardwareMap)

                doUpdate = false
            }
            telemetry.addData("Current mode", if(isBACKUP == 1)"BACKUP" else "PRIMARY")
            telemetry.addData("Press 'Y' to toggle the backup mode (doing so will also re-initialize the replay fiels).", "")
            telemetry.update()

            if(gamepad1.y && (time - lastPress > 1.0 || lastPress == -1.0)) {
                isBACKUP = if(isBACKUP == 0) 1 else 0
                lastPress = time
                doUpdate = true
            }
        }

        TFOD.activate()
        var sample_position = SamplePosition.N_A
        val timer_it = ElapsedTime()
        timer_it.reset()

        while(opModeIsActive() && timer_it.seconds() < 5) {
            sample_position = findSample_THREE(TFOD.updatedRecognitions)
            if (sample_position != org.firstinspires.ftc.teamcode.roverruckus.minibit.autonomous.AutonomousBase.SamplePosition.N_A && timer_it.seconds() > 1) break
        }

        val USE_RECORD = when(sample_position) {
            SamplePosition.LEFT -> RECORD_LEFT
            SamplePosition.N_A,
            SamplePosition.CENTER -> RECORD_MIDDLE
            SamplePosition.RIGHT -> RECORD_RIGHT
        }

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
        hardwareMap.appContext.fileList().filter { it.contains(TimeStampedData.FILE_PREFIX) }.forEach {
            if(!it.equals(FILE_LEFT_SAMPLE[0])  || !it.equals(FILE_LEFT_SAMPLE[1])  ||
               !it.equals(FILE_MIDDLE_SAMPLE[0])|| !it.equals(FILE_MIDDLE_SAMPLE[1])||
               !it.equals(FILE_RIGHT_SAMPLE[0]) || !it.equals(FILE_RIGHT_SAMPLE[1])) return false
        }
        return true
    }
}