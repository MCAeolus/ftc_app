package org.firstinspires.ftc.teamcode.roverruckus.ruckus.opmodes.autonomous

import android.content.Context
import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.CRServo
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

    val defaultDir = "${TimeStampedData.REPLAY_DIRECTORY_PREFIX}AutonomousRunner"
    val operatorName = "__OPERATION"

    override fun runOpMode() {

        val subDir = ""
        var selectorLoc = 0

        var down_DPADpressed = false
        var buttonPressed = true

        while(!isStarted) {

            if(gamepad1.dpad_down && !down_DPADpressed) {

            }

            val totalDir = defaultDir + subDir

            val dir = hardwareMap.appContext.getDir(totalDir, Context.MODE_PRIVATE)
            val lister = dir.list { _ : File, s : String -> s.startsWith(TimeStampedData.REPLAY_DIRECTORY_PREFIX)}

            send("Please select the proper operation mode for the Autonomous Runner.")
            send("Use UP and DOWN DPAD buttons to move selector, use 'A' to make the selection.")
            send("")

            for(i in 0 until lister.size)
                send("${if(selectorLoc == i)">>>" else "   "}${lister[i]}", if(isOperationDirectory(totalDir + lister[i])) "OPERATION MODE" else "->")

            telemetry.update()
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

                        is CRServo -> device.power = it.data[0]

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

    private fun send(h : String, p : String = "", separator : String = "", update : Boolean = false, refresh : Boolean = true) {
        telemetry.captionValueSeparator = separator
        telemetry.isAutoClear = refresh
        telemetry.addData(h, p)
        if(update)telemetry.update()
    }

    private fun isOperationDirectory(dirPath : String) = hardwareMap.appContext.getDir(dirPath, Context.MODE_PRIVATE).list().contains(operatorName)

    }
}